import baostock as bs

from reptile.items import StockBasicItem, StockBasicBatchItem
from reptile.spiders.base import BaseStockSpider
from reptile.util import list_util


def query_all_stock_basic():
    bs.login()
    result = []
    stock_info = bs.query_stock_basic()
    for info in stock_info.data:
        code = info[0]
        code_name = info[1]
        ipo_date = info[2]
        out_date = info[3]
        type = info[4]
        status = info[5]
        if not type or type != '1':
            continue
        if status == '0':
            continue
        info = StockBasicItem(code=code,
                              name=code_name,
                              ipoDate=ipo_date,
                              outDate=out_date,
                              type=type,
                              status=True)
        result.append(dict(info))
    bs.logout()
    return result


class StockBasicSpider(BaseStockSpider):
    name = "stock_basic"
    start_urls = ['https://www.baidu.com']

    def parse(self, response, **kwargs):
        result = query_all_stock_basic()
        split_list = list_util.list_split(result, 400)

        if not split_list:
            return None

        for r in split_list:
            yield StockBasicBatchItem(list=r)
