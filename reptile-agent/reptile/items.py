# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class BaseItem(scrapy.Item):
    pass


class ProductItem(BaseItem):
    id = scrapy.Field()
    reptileRuleId = scrapy.Field()
    platform = scrapy.Field()
    category = scrapy.Field()
    subject = scrapy.Field()  # 标题
    description = scrapy.Field()
    brand = scrapy.Field()
    detailUrl = scrapy.Field()  # detialurl
    mainImageUrl = scrapy.Field()
    extraImageUrls = scrapy.Field()
    language = scrapy.Field()
    feature = scrapy.Field()
    skuList = scrapy.Field()


class StockHQItem(BaseItem):
    code = scrapy.Field()
    hqTime = scrapy.Field()
    price = scrapy.Field()
    changePercent = scrapy.Field()
    change = scrapy.Field()


class StockHQBatchItem(BaseItem):
    list = scrapy.Field()


class StockBasicItem(BaseItem):
    code = scrapy.Field()
    name = scrapy.Field()
    ipoDate = scrapy.Field()
    outDate = scrapy.Field()
    type = scrapy.Field()
    status = scrapy.Field()


class StockBasicBatchItem(BaseItem):
    list = scrapy.Field()


class StockDailyItem(BaseItem):
    code = scrapy.Field()
    date = scrapy.Field()
    open = scrapy.Field()
    high = scrapy.Field()
    low = scrapy.Field()
    close = scrapy.Field()
    preClose = scrapy.Field()
    change = scrapy.Field()
    changePercent = scrapy.Field()
    volume = scrapy.Field()
    amount = scrapy.Field()
    turn = scrapy.Field()
    trace_status = scrapy.Field()
    peTtm = scrapy.Field()


class StockDailyBatchItem(BaseItem):
    list = scrapy.Field()
