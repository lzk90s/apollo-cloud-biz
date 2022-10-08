"""
This module provides core code for picking up proxies
"""
import json
import time

# from logger import client_logger
from redis import client

from .. import client_logger
from ..config.redis_key import PROXY_DETAIL_HASHMAP
from ..utils import decode_all
from ..utils.functools import decode


class IPFetcherMixin:
    def __init__(self, usage, score_queue, weight_queue, ttl_queue, speed_queue,
                 longest_response_time, lowest_score, lowest_weight, ttl_validated_resource,
                 min_pool_size):
        self.usage = usage
        self.score_queue = score_queue
        self.weight_queue = weight_queue
        self.ttl_queue = ttl_queue
        self.speed_queue = speed_queue
        self.longest_response_time = longest_response_time
        self.lowest_score = lowest_score
        self.lowest_weight = lowest_weight
        self.ttl_validated_resource = ttl_validated_resource
        self.min_pool_size = min_pool_size

    def get_available_proxies(self, conn):
        assert isinstance(conn, client.Redis)

        """core algrithm to get proxies from redis"""
        start_time = int(time.time()) - self.ttl_validated_resource * 60
        pipe = conn.pipeline(False)
        pipe.zrevrangebyscore(self.score_queue, '+inf', self.lowest_score)
        pipe.zrevrangebyscore(self.ttl_queue, '+inf', start_time)
        pipe.zrangebyscore(self.speed_queue, 0, 1000 * self.longest_response_time)
        scored_proxies, ttl_proxies, speed_proxies = pipe.execute()

        scored_proxies = set(scored_proxies)
        ttl_proxies = set(ttl_proxies)
        speed_proxies = set(speed_proxies)

        proxies = scored_proxies & ttl_proxies & speed_proxies
        proxies = decode_all(proxies)

        client_logger.debug("usage={}, scored_proxies={}, ttl_proxies={}, speed_proxies={}, proxies={}"
                            .format(self.usage,
                                    len(scored_proxies),
                                    len(ttl_proxies),
                                    len(speed_proxies),
                                    len(proxies)))

        res = [self.build_proxy_info(conn, p) for p in proxies]
        res.sort(key=lambda x: x['ttl'], reverse=False)
        return res

    def build_proxy_info(self, conn, proxy):
        pipe = conn.pipeline(False)
        pipe.hget(PROXY_DETAIL_HASHMAP, proxy)
        pipe.zscore(self.score_queue, proxy)
        pipe.zscore(self.weight_queue, proxy)
        pipe.zscore(self.ttl_queue, proxy)
        pipe.zscore(self.speed_queue, proxy)
        detail, score, weight, ttl, speed = pipe.execute()

        proxy_info = {'url': proxy}
        if detail:
            detail = json.loads(decode(detail))
            proxy_info['country'] = detail['country']
        if score:
            proxy_info['score'] = round(score, 2)
        if weight:
            proxy_info['weight'] = round(weight, 2)
        if ttl:
            proxy_info['ttl'] = int(time.time()) - int(ttl)
        if speed:
            proxy_info['speed'] = round(speed, 2)
        return proxy_info
