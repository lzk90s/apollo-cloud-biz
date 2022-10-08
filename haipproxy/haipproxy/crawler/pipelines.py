"""
scrapy pipelines for storing proxy ip infos.
"""
import json
from urllib.parse import urlparse

from twisted.internet.threads import deferToThread

from .items import (
    ProxyValidateItem, ProxyScore, ProxyWeight, ProxyVerifiedTime, ProxySpeed)
from .validators import ALL_VALIDATOR_QUEUES
from .. import crawler_logger
from ..config.redis_key import (
    DATA_ALL_SET, CRAWLER_COUNTER_HASHMAP,
    PROXY_DETAIL_HASHMAP, INIT_VALIDATOR_HTTP_QUEUE_LIST
)
from ..config.settings import (REDIS_DB)
from ..utils import get_redis_conn, net_util


class BasePipeline:
    def open_spider(self, spider):
        self.redis_con = get_redis_conn(db=REDIS_DB)

    def process_item(self, item, spider):
        return deferToThread(self._process_item, item, spider)

    def _process_item(self, item, spider):
        raise NotImplementedError


class ProxyIPPipeline(BasePipeline):
    def _process_item(self, item, spider):
        url = item.get('url', None)
        if not url:
            return item

        pipeline = self.redis_con.pipeline()
        not_exists = pipeline.sadd(DATA_ALL_SET, url)
        if not_exists:
            # if 'socks4' in url:
            #     pipeline.rpush(ALL_INIT_VALIDATOR_QUEUE_LIST.get(INIT_VALIDATOR_SOCK4), url)
            # elif 'socks5' in url:
            #     pipeline.rpush(ALL_INIT_VALIDATOR_QUEUE_LIST.get(INIT_VALIDATOR_SOCK5), url)
            # else:
            pipeline.rpush(INIT_VALIDATOR_HTTP_QUEUE_LIST, url)
            crawler_logger.info("store proxy {} to init queue".format(url))
        pipeline.hincrby(CRAWLER_COUNTER_HASHMAP, item['source'])
        pipeline.execute()
        return item


class ProxyCommonPipeline(BasePipeline):
    def _process_item(self, item, spider):
        assert isinstance(item, ProxyValidateItem)
        url = item['url']
        incr = self._process_score_item(url, item['incr'], item['score_dimension'], spider)
        self._process_weight_item(url, incr, item['weight_dimension'], spider)
        self._process_verified_item(url, incr, item['verified_time_dimension'], spider)
        self._process_speed_item(url, incr, item['speed_dimension'], spider)
        return item

    def _process_score_item(self, url: str, incr: float, info: ProxyScore, spider):
        latest_score = self.redis_con.zscore(info.queue, url)
        new_incr = self._calculate_incr(incr, latest_score)
        if '-inf' == new_incr:
            crawler_logger.info("remove proxy {}, queue={}, score={}".format(url, info.queue, latest_score))
            self.redis_con.zrem(info.queue, url)
            self._remove_unused_proxy(url)
        elif not latest_score:
            crawler_logger.info("add proxy {}, queue={}, score={}".format(url, info.queue, info.score))
            self.redis_con.zadd(info.queue, {url: info.score})
            self._save_proxy_info(url)
        elif new_incr:
            crawler_logger.info(
                "update proxy {}, queue={}, score={}, incr={}".format(url, info.queue, latest_score, new_incr))
            self.redis_con.zincrby(info.queue, new_incr, url)
        else:
            raise ValueError()
        return new_incr

    def _process_weight_item(self, url: str, incr: float, info: ProxyWeight, spider):
        if incr == '-inf':
            self.redis_con.zrem(info.queue, url)
        elif incr > 0:
            self.redis_con.zadd(info.queue, {url: info.weight})

    def _process_verified_item(self, url: str, incr: float, info: ProxyVerifiedTime, spider):
        if incr == '-inf':
            self.redis_con.zrem(info.queue, url)
        elif incr > 0:
            self.redis_con.zadd(info.queue, {url: info.verified_time})

    def _process_speed_item(self, url: str, incr: float, info: ProxySpeed, spider):
        if incr == '-inf':
            self.redis_con.zrem(info.queue, url)
        elif incr > 0:
            self.redis_con.zadd(info.queue, {url: info.response_time})

    def _remove_unused_proxy(self, url):
        used = False
        for k, v in ALL_VALIDATOR_QUEUES.items():
            queue = v.SCORE_QUEUE_ZSET
            if self.redis_con.zrank(queue, url):
                used = True
                break
        if not used:
            pipe = self.redis_con.pipeline(True)
            pipe.srem(DATA_ALL_SET, url)
            pipe.hdel(PROXY_DETAIL_HASHMAP, url)
            pipe.execute()
            crawler_logger.info("remove unused proxy {} from data all set".format(url))

    @classmethod
    def _calculate_incr(cls, incr, score):
        if not score:
            return incr
        if (incr == '-inf') or (incr < 0 and score <= 1):
            new_incr = '-inf'
        elif incr < 0 and 1 < score:
            new_incr = incr
        elif incr > 0 and score < 10:
            new_incr = incr
        elif incr > 0 and score >= 10:
            new_incr = round(10 / score, 2)
        else:
            new_incr = '-inf'
        return new_incr

    @classmethod
    def _get_proxy_info(cls, proxy):
        return json.dumps(net_util.get_ip_info(urlparse(proxy).hostname), ensure_ascii=False)

    def _save_proxy_info(self, url):
        detail = self.redis_con.hexists(PROXY_DETAIL_HASHMAP, url)
        if not detail:
            self.redis_con.hset(PROXY_DETAIL_HASHMAP, url, self._get_proxy_info(url))
