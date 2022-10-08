import datetime
import io

import pandas as pd
from scrapy import Request

from reptile.client import stock_client
from reptile.items import StockDailyBatchItem
from reptile.spiders.base import BaseStockSpider


class StockDailySpider(BaseStockSpider):
    name = "stock_daily"

    custom_settings = {
        'DOWNLOAD_DELAY': 3,
        'RANDOMIZE_DOWNLOAD_DELAY': True,
        'CONCURRENT_REQUESTS_PER_DOMAIN': 5
    }

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    def start_requests(self):
        stock_codes = stock_client.get_all_stock_codes()
        for it in stock_codes:
            now_time = datetime.datetime.now()
            days_delta = 0
            start_day = (now_time + datetime.timedelta(days=days_delta)).strftime("%Y%m%d")
            end_day = now_time.strftime("%Y%m%d")
            url = self.daily_k_url(self.code_from_standard(it), start_day, end_day)
            yield Request(url=url, callback=self.parse, meta={'code': it})

    def parse(self, response, **kwargs):
        info = pd.read_csv(io.BytesIO(response.body), encoding='gbk')

        result = []
        for row in info.iterrows():
            it = row[1].to_dict()
            k_data = {
                'code': response.request.meta['code'],
                'date': it['日期'],
                'open': it['开盘价'],
                'close': it['收盘价'],
                'preClose': it['前收盘'],
                'high': it['最高价'],
                'low': it['最低价'],
                'change': it['涨跌额'],
                'changePercent': it['涨跌幅'],
                'volume': it['成交量'],
                'amount': it['成交金额'],
                'turn': it['换手率'],
                'tcap': it['总市值'],
                'mcap': it['流通市值'],
            }
            if not k_data['volume']:
                self.logger.warn("ignore invalid {}".format(k_data))
                continue
            result.append(k_data)

        if not result:
            return None
        yield StockDailyBatchItem(list=result)

    @classmethod
    def daily_k_url(cls, code, start, end=None):
        stock_daily_k_url_mod = "http://quotes.money.163.com/service/chddata.html?code={}&start={}&end={}"
        return stock_daily_k_url_mod.format(code, start, end)

    @staticmethod
    def code_from_standard(code):
        if str(code).startswith('sh.'):
            return '0' + str(code[3:])
        elif str(code).startswith('sz.'):
            return '1' + str(code[3:])
        else:
            raise ValueError()

    @staticmethod
    def code_to_standard(code: str):
        if code.startswith('\''):
            code = code[1:]
        type_flag = code[0:1]
        real_code = code[1:]
        if type_flag == '0':
            return 'sh.' + real_code
        elif type_flag == '1':
            return 'sz.' + real_code
        else:
            raise ValueError()
