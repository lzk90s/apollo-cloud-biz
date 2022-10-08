import json
import time

from prometheus_client import start_http_server, generate_latest
from prometheus_client.core import (
    CounterMetricFamily, GaugeMetricFamily,
    REGISTRY
)

from haipproxy.config.crawler_rules import (CRAWLER_TASKS)
from haipproxy.config.redis_key import (
    DATA_ALL_SET, CRAWLER_COUNTER_HASHMAP, PROXY_DETAIL_HASHMAP, INIT_VALIDATOR_HTTP_QUEUE_LIST,
    VALIDATOR_HTTPS_QUEUE, VALIDATOR_WALMART_QUEUE
)
from haipproxy.config.settings import (EXPORTER_LISTEN_HOST, EXPORTER_LISTEN_PORT, LOWEST_SCORE)
from haipproxy.utils import get_redis_conn


class CustomCollector:
    def __init__(self):
        self.conn = get_redis_conn()

    def collect(self):
        pipe = self.conn.pipeline(False)
        pipe.scard(DATA_ALL_SET)
        pipe.llen(INIT_VALIDATOR_HTTP_QUEUE_LIST)
        pipe.hgetall(CRAWLER_COUNTER_HASHMAP)
        pipe.zrevrangebyscore(VALIDATOR_HTTPS_QUEUE.SCORE_QUEUE_ZSET, '+inf', LOWEST_SCORE)
        pipe.zrevrangebyscore(VALIDATOR_WALMART_QUEUE.SCORE_QUEUE_ZSET, '+inf', LOWEST_SCORE)
        r = pipe.execute()

        total_proxies = r[0]
        init_proxies = r[1]
        crawler_counter = dict(r[2])
        https_validated_proxies = r[3]
        walmart_validated_proxies = r[4]

        yield CounterMetricFamily('total_proxies', 'total proxies', total_proxies)
        yield GaugeMetricFamily('init_proxies', 'total init proxies', init_proxies)

        tmp_crawler_counter = {}
        for task in CRAWLER_TASKS:
            tmp_crawler_counter[task.get('name')] = 0
        for k, v in crawler_counter.items():
            tmp_crawler_counter[bytes.decode(k)] = bytes.decode(v)
        crawler_counter = CounterMetricFamily('crawler_counter', 'crawler counter', labels=['source'])
        for k, v in tmp_crawler_counter.items():
            crawler_counter.add_metric([k], v)
        yield crawler_counter

        validated_proxy_gauge = GaugeMetricFamily('validated_proxies', 'validated proxy', labels=['type'])
        validated_proxy_gauge.add_metric(['https'], len(https_validated_proxies))
        validated_proxy_gauge.add_metric(['walmart'], len(walmart_validated_proxies))
        yield validated_proxy_gauge

        proxies = set(https_validated_proxies) | set(walmart_validated_proxies)
        country_count = {}
        for proxy in proxies:
            country = self.get_proxy_country(proxy)
            if not country:
                country = 'Unknown'
                print("Unknown country for proxy {}".format(proxy))
            if country in country_count:
                country_count[country] = int(country_count[country]) + 1
            else:
                country_count[country] = 1
        country_gauge = GaugeMetricFamily('country_count', 'country count', labels=['country'])
        for k, v in country_count.items():
            country_gauge.add_metric([k], v)
        yield country_gauge

    def get_proxy_country(self, proxy):
        r = self.conn.hget(PROXY_DETAIL_HASHMAP, proxy)
        if not r:
            return None
        detail = json.loads(bytes.decode(r))
        if not detail:
            return None
        return detail['country']


def register():
    REGISTRY.register(CustomCollector())
    return REGISTRY


def get_latest_metrics(registry):
    return generate_latest(registry)


def exporter_start():
    print('starting server http://{}:{}/metrics'.format(EXPORTER_LISTEN_HOST, EXPORTER_LISTEN_PORT))
    register()
    start_http_server(EXPORTER_LISTEN_PORT, addr=EXPORTER_LISTEN_HOST)
    while True:
        time.sleep(5)
