"""
Basic proxy ip crawler.
"""
from .base import BaseSpider
from ..items import ProxyUrlItem
from ..redis_spiders import RedisSpider

# notice multi inheritance order in python
from ...config.redis_key import CRAWLER_COMMON_QUEUE_ZSET


class CommonSpider(BaseSpider, RedisSpider):
    name = 'common'
    task_queue = CRAWLER_COMMON_QUEUE_ZSET

    def __init__(self):
        super().__init__()
        self.parser_maps.setdefault('myproxy', self.parse_my_proxy)

    def parse_my_proxy(self, response, task_name):
        protocols = None
        if self.exists(response.url, 'socks-4'):
            protocols = ['socks4']
        if self.exists(response.url, 'socks-5'):
            protocols = ['socks5']

        items = list()
        infos = response.css('.list ::text').extract()
        for info in infos:
            if ':' not in info:
                continue
            pos = info.find('#')
            if pos != -1:
                info = info[:info.find('#')]
            ip, port = info.split(':')
            protocols = self.default_protocols if not protocols else protocols
            for protocol in protocols:
                items.append(ProxyUrlItem(url=self.construct_proxy_url(protocol, ip, port), source=task_name))
        return items
