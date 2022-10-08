from datetime import datetime

from reptile.client import stock_client
from reptile.items import StockHQItem, StockHQBatchItem
from reptile.spiders.base import BaseStockSpider
from reptile.util import json_util, list_util


class StockHQSpider(BaseStockSpider):
    name = "stock_hq"

    custom_settings = {
        'DOWNLOAD_DELAY': 2,
        'RANDOMIZE_DOWNLOAD_DELAY': True,
        'CONCURRENT_REQUESTS_PER_DOMAIN': 5
    }

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        max_tcap = self.rule_opts['maxTcap'] if 'maxTcap' in self.rule_opts else None
        stock_codes = stock_client.get_all_stock_codes(max_tcap)
        split_codes = list_util.list_split(stock_codes, 400)
        for it in split_codes:
            url = self.hq_url([self.code_from_standard(s) for s in it])
            self.start_urls.append(url)

    def parse(self, response, **kwargs):
        skip = len('_ntes_quote_callback(')
        json_data = str(response.text)[skip: -2]
        if not json_data:
            return None

        info = json_util.json2dict(json_data)

        result = []
        for k, v in info.items():
            if 'updown' not in v:
                continue
            hq_time = datetime.strptime(v['update'], '%Y/%m/%d %H:%M:%S').strftime("%Y-%m-%d %H:%M:%S")
            stock_item = StockHQItem()
            stock_item['code'] = self.code_to_standard(k)
            stock_item['hqTime'] = hq_time
            stock_item['price'] = v['price']
            stock_item['changePercent'] = round(float(v['percent']) * 100, 2)
            stock_item['change'] = v['updown']
            result.append(dict(stock_item))

        if not result:
            return None

        yield StockHQBatchItem(list=result)

    @classmethod
    def hq_url(cls, codes):
        stock_hq_url_mod = "https://api.money.126.net/data/feed/{},money.api%5D"
        return stock_hq_url_mod.format(','.join(codes))

    @staticmethod
    def code_from_standard(code):
        if str(code).startswith('sh.'):
            return '0' + str(code[3:])
        elif str(code).startswith('sz.'):
            return '1' + str(code[3:])
        else:
            raise ValueError()

    @staticmethod
    def code_to_standard(code):
        type_flag = code[0:1]
        real_code = code[1:]
        if type_flag == '0':
            return 'sh.' + real_code
        elif type_flag == '1':
            return 'sz.' + real_code
        else:
            raise ValueError()
