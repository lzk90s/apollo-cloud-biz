# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html
import logging

from reptile.client import product_client, stock_client
from reptile.items import ProductItem, StockHQItem, BaseItem, StockDailyItem, StockBasicItem, StockHQBatchItem, \
    StockBasicBatchItem, StockDailyBatchItem
from reptile.util import json_util


class ReptilePipeline(object):
    uploader_map = {
        ProductItem: product_client.upload_product,
        StockHQItem: stock_client.upload_stock_hq,
        StockHQBatchItem: stock_client.upload_stock_hq,
        StockBasicItem: stock_client.upload_stock_basic,
        StockBasicBatchItem: stock_client.upload_stock_basic,
        StockDailyItem: stock_client.upload_stock_daily,
        StockDailyBatchItem: stock_client.upload_stock_daily,
    }

    def process_item(self, item: BaseItem, spider):
        obj = dict(item)
        #logging.debug("upload {}\n{}".format(item.__class__.__name__, json_util.obj2json(obj)))
        fun = self.get_uploader(item)
        if not fun:
            raise Exception("Unsupported type")
        fun(obj['list'])

    def get_uploader(self, item):
        for k, v in self.uploader_map.items():
            if isinstance(item, k):
                return v
        return None

    def close_spider(self, spider):
        logging.info("close spider")
