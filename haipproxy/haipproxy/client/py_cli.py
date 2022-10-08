"""
python client for haipproxy
"""
import random
import threading
import time

from .core import IPFetcherMixin
from ..config.redis_key import (DATA_ALL_SET)
from ..config.settings import (
    TTL_VALIDATED_RESOURCE, LONGEST_RESPONSE_TIME,
    LOWEST_SCORE, LOWEST_TOTAL_PROXIES,
    LOWEST_WEIGHT)
from ..crawler.validators import ALL_VALIDATOR_QUEUES
from ..utils import get_redis_conn

__all__ = ['ProxyFetcher']

lock = threading.RLock()


class Strategy:
    strategy = None

    def check(self, strategy):
        return self.strategy == strategy

    def get_proxies_by_stragery(self, pool, filter_opts):
        """
        :param pool: pool is a list, which is mutable
        :return:
        """
        raise NotImplementedError

    def process_feedback(self, pool, res, proxy, **kwargs):
        """
        :param pool: ProxyFetcher's pool
        :param res: success or failure
        :param proxy: proxy ip
        :param kwargs: response time or expected response time
        :return: None
        """
        raise NotImplementedError

    def filter_with_opts(self, pool, filter_opts: dict) -> list:
        if not filter_opts:
            return pool

        tmp_pool = []
        for k, v in filter_opts.items():
            tmp_pool.extend([p for p in pool if k == 'country' and p[k] == v])
        return tmp_pool

    def remove_proxy_in_pool(self, pool: list, proxy: str):
        for p in pool:
            if pool == p:
                pool.remove(p)
                return p
        return None


class RobinStrategy(Strategy):
    def __init__(self):
        super().__init__()
        self.strategy = 'robin'

    def get_proxies_by_stragery(self, pool: list, filter_opts):
        if not pool:
            return None

        tmp_pool = self.filter_with_opts(pool, filter_opts)
        if not tmp_pool:
            return None

        proxy = tmp_pool.pop(0)
        pool.append(self.remove_proxy_in_pool(pool, proxy))
        return proxy

    def process_feedback(self, pool, res, proxy, **kwargs):
        if res == 'failure':
            if pool[-1] == proxy:
                with lock:
                    if pool[-1] == proxy:
                        pool.pop()
        return


class RandomStrategy(Strategy):
    def __init__(self):
        self.strategy = 'random'

    def get_proxies_by_stragery(self, pool, filter_opts):
        if not pool:
            return None

        tmp_pool = self.filter_with_opts(pool, filter_opts)
        if not tmp_pool:
            return None

        proxy = random.choice(tmp_pool)
        return proxy

    def process_feedback(self, pool, res, proxy, **kwargs):
        pass


class GreedyStrategy(Strategy):
    def __init__(self):
        self.strategy = 'greedy'

    def get_proxies_by_stragery(self, pool, filter_opts):
        if not pool:
            return None
        tmp_pool = self.filter_with_opts(filter_opts)
        if not tmp_pool:
            return None
        return tmp_pool[0]

    def process_feedback(self, pool, res, proxy, **kwargs):
        if res == 'failure':
            if pool[0] == proxy:
                with lock:
                    if pool[0] == proxy:
                        pool.pop(0)
            return
        expected_time = kwargs.get('expected')
        real_time = kwargs.get('real')
        if expected_time * 1000 < real_time:
            pool.pop(0)
            pool.append(proxy)


class ProxyFetcher(IPFetcherMixin):
    def __init__(self, usage, strategy='robin', fast_response=5, validator_queue_maps=ALL_VALIDATOR_QUEUES,
                 longest_response_time=LONGEST_RESPONSE_TIME, lowest_score=LOWEST_SCORE, lowest_weight=LOWEST_WEIGHT,
                 ttl_validated_resource=TTL_VALIDATED_RESOURCE, min_pool_size=LOWEST_TOTAL_PROXIES,
                 all_data=DATA_ALL_SET, redis_args=None):
        """
        :param usage: one of SCORE_MAPS's keys, such as https
        :param strategy: the load balance of proxy ip, the value is
        one of ['robin', 'greedy']
        :param fast_response: if you use greedy strategy, it will be needed to
        decide whether a proxy ip should continue to be used
        :param score_map: score map of your project, default value is SCORE_MAPS in haipproxy.config.settings
        :param ttl_map: ttl map of your project, default value is TTL_MAPS in haipproxy.config.settings
        :param speed_map: speed map of your project, default value is SPEED_MAPS in haipproxy.config.settings
        :param ttl_validated_resource: time of latest validated proxies
        :param min_pool_size: min pool size of self.pool
        :param all_data: all proxies are stored in this set
        :param redis_args: redis connetion args, it's a dict, whose keys include host, port, db and password
        """
        # if there are multi parent classes, super is only used for the first parent according to MRO
        if usage not in validator_queue_maps.keys():
            # client_logger.warning('task value is invalid, https task will be used')
            usage = 'https'
        queue = validator_queue_maps.get(usage)
        score_queue = queue.SCORE_QUEUE_ZSET
        weight_queue = queue.WEIGHT_QUEUE_ZSET
        ttl_queue = queue.TTL_QUEUE_ZSET
        speed_queue = queue.SPEED_QUEUE_ZSET
        super().__init__(usage, score_queue, weight_queue, ttl_queue, speed_queue, longest_response_time,
                         lowest_score, lowest_weight, ttl_validated_resource, min_pool_size)
        self.strategy = strategy
        # pool is a FIFO queue
        self.pool = list()
        self.min_pool_size = min_pool_size
        self.fast_response = fast_response
        self.all_data = all_data
        self.handlers = [RobinStrategy(), GreedyStrategy(), RandomStrategy()]
        if isinstance(redis_args, dict):
            self.conn = get_redis_conn(**redis_args)
        else:
            self.conn = get_redis_conn()
        t = threading.Thread(target=self._refresh_periodically)
        t.setDaemon(True)
        t.start()

    def get_proxy(self, filter_opts):
        """
        get one available proxy from redis, if there's none, None is returned
        :return:
        """
        proxy = None
        self.refresh()
        for handler in self.handlers:
            if handler.strategy == self.strategy:
                proxy = handler.get_proxies_by_stragery(self.pool, filter_opts)
        return proxy

    def get_proxies(self):
        proxies = self.get_available_proxies(self.conn)
        # client_logger.info('{} proxies have been fetched'.format(len(proxies)))
        self.pool.extend(proxies)
        return self.pool

    def get_latest_proxies(self):
        return self.get_available_proxies(self.conn)

    def proxy_feedback(self, res, proxy, response_time=None):
        """
        client should give feedbacks after executing get_proxy()
        :param res: value of 'success' or 'failure'
        :param proxy: proxy ip
        :param response_time: the response time using current proxy ip
        """
        for handler in self.handlers:
            if handler.strategy == self.strategy:
                handler.process_feedback(self.pool, res,
                                         proxy, real=response_time,
                                         expected=self.fast_response)

    def refresh(self):
        if len(self.pool) < self.min_pool_size:
            self.get_proxies()

    def delete_proxy(self, proxy):
        pipe = self.conn.pipeline()
        pipe.srem(self.all_data, proxy)
        pipe.zrem(self.score_queue, proxy)
        pipe.zrem(self.speed_queue, proxy)
        pipe.zrem(self.ttl_queue, proxy)
        pipe.zrem(self.weight_queue, proxy)
        pipe.execute()

    def _refresh_periodically(self):
        while True:
            # remove invalid proxies
            latest_proxies = self.get_latest_proxies()
            tmp_pool = [p for p in self.pool if p in latest_proxies]
            self.pool.clear()
            self.pool.extend(tmp_pool)

            # get proxies
            if len(self.pool) < int(self.min_pool_size):
                self.get_proxies()

            time.sleep(5)

    def filter_with_opts(self, filter_opts):
        for handler in self.handlers:
            if handler.strategy == self.strategy:
                return handler.filter_with_opts(self.get_latest_proxies(), filter_opts)
        return None
