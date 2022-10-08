"""
Proxy spider for the websites blocked by gfw.
"""
import json
import re

from .common_spider import CommonSpider
from ..items import ProxyUrlItem
from ...config.redis_key import CRAWLER_GFW_QUEUE_ZSET


class GFWSpider(CommonSpider):
    name = 'gfw'
    proxy_mode = 2
    task_queue = CRAWLER_GFW_QUEUE_ZSET

    def __init__(self):
        super().__init__()
        self.parser_maps.setdefault('xroxy', self.parse_xroxy)
        self.parser_maps.setdefault('gather_proxy', self.parse_gather_proxy)

    def parse_gather_proxy(self, response, task_name):
        items = list()
        infos = response.css('script::text').re(r'gp.insertPrx\((.*)\)')
        for info in infos:
            info = info.lower()
            detail = json.loads(info)
            ip = detail.get('proxy_ip')
            port = detail.get('proxy_port')
            protocols = self.procotol_extractor(info)
            for protocol in protocols:
                items.append(ProxyUrlItem(url=self.construct_proxy_url(protocol, ip, port), source=task_name))
        return items

    def parse_xroxy(self, response, task_name):
        items = list()
        ip_extract_pattern = '">(.*)\\n'
        infos = response.xpath('//tr').css('.row1') + response.xpath('//tr').css('.row0')
        for info in infos:
            m = re.search(ip_extract_pattern, info.css('a')[1].extract())
            if m:
                ip = m.group(1)
                port = info.css('a::text')[2].extract()
                protocol = info.css('a::text')[3].extract().lower()
                if protocol in ['socks4', 'socks5']:
                    items.append(ProxyUrlItem(url=self.construct_proxy_url(protocol, ip, port)))
                elif protocol == 'transparent':
                    continue
                else:
                    items.append(ProxyUrlItem(url=self.construct_proxy_url('http', ip, port)))
                    is_ssl = info.css('a::text')[4].extract().lower() == 'true'
                    if is_ssl:
                        items.append(ProxyUrlItem(url=self.construct_proxy_url('https', ip, port), source=task_name))

        return items
