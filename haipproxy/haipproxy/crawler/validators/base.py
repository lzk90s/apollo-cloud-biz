"""
Useful base class for all the validators.
"""
import time
from urllib.parse import urlparse

from scrapy.spidermiddlewares.httperror import HttpError
from twisted.internet.error import (
    TimeoutError, TCPTimedOutError)

# from logger import crawler_logger
from ..items import (
    ProxyScore, ProxyVerifiedTime,
    ProxySpeed, ProxyWeight, ProxyValidateItem)
from ...config.settings import VALIDATOR_CHECK_PORTS
from ...logger import crawler_logger
from ...utils import net_util


class BaseValidator:
    """base validator for all the validators"""
    name = 'base'

    init_score = 5
    # slow down each spider
    custom_settings = {
        'CONCURRENT_REQUESTS': 50,
        'CONCURRENT_REQUESTS_PER_DOMAIN': 20,
        'RETRY_ENABLED': False,
        'DOWNLOADER_MIDDLEWARES': {
            'haipproxy.crawler.middlewares.RequestStartProfileMiddleware': 500,
            'haipproxy.crawler.middlewares.RequestEndProfileMiddleware': 500,
        },
        'ITEM_PIPELINES': {
            'haipproxy.crawler.pipelines.ProxyCommonPipeline': 200,
        }
    }
    use_set = True
    success_key = ''
    task_queue = None
    # all the children validators must specify the following args
    # unless you overwrite the set_item_queue() method
    urls = None
    check_ports = VALIDATOR_CHECK_PORTS
    all_validator_queues = None

    def parse(self, response):
        proxy = response.meta.get('proxy')
        speed = response.meta.get('speed')
        if response.status != 200:
            crawler_logger.info("None 200 status {}, proxy {}".format(response.status, proxy))
        url = response.url
        transparent = self.is_transparent(response)
        if transparent:
            return
        score_incr = 1 if self.is_ok(response) else '-inf'
        opened_ports = self.check_opened_port(urlparse(proxy).hostname)
        weight = self.calculate_weight(self.init_score, speed, opened_ports)
        items = self.set_item_queue(url, proxy, self.init_score, score_incr, speed, weight)
        for item in items:
            yield item

    def parse_error(self, failure):
        request = failure.request
        proxy = request.meta.get('proxy')
        crawler_logger.error('Proxy {} has failed, {} is raised'.format(proxy, failure))
        if failure.check(TimeoutError, TCPTimedOutError):
            decr = -1
        elif failure.check(HttpError):
            decr = -3
        else:
            decr = '-inf'

        items = self.set_item_queue(request.url, proxy, self.init_score, decr)
        for item in items:
            yield item

    def is_ok(self, response):
        return self.success_key in response.text

    def set_item_queue(self, url, proxy, score, score_incr, speed=0, weight=0) -> list:
        queue = self.all_validator_queues.get(self.name)
        score_item = ProxyScore(queue.SCORE_QUEUE_ZSET, score)
        weight_item = ProxyWeight(queue.WEIGHT_QUEUE_ZSET, weight)
        ttl_item = ProxyVerifiedTime(queue.TTL_QUEUE_ZSET, int(time.time()))
        speed_item = ProxySpeed(queue.SPEED_QUEUE_ZSET, speed)
        item = ProxyValidateItem(url=proxy,
                                 incr=score_incr,
                                 score_dimension=score_item,
                                 weight_dimension=weight_item,
                                 verified_time_dimension=ttl_item,
                                 speed_dimension=speed_item)
        return [item]

    def check_opened_port(self, ip):
        if not self.check_ports:
            return []
        check_ports = list(self.check_ports.keys())
        check_ports = list(set(check_ports))
        return net_util.check_opened_port(ip, check_ports)

    def calculate_weight(self, score, speed_time, open_ports):
        weight = score * 1000
        for port in open_ports:
            weight += self.check_ports.get(port)
        weight += 10000 - speed_time
        return weight

    def is_transparent(self, response):
        return False
