"""
Scrapy items for haiproxy
"""
import json

import scrapy


class ProxyUrlItem(scrapy.Item):
    url = scrapy.Field()
    incr = scrapy.Field()
    source = scrapy.Field()


class ProxyScore:
    def __init__(self, queue, score):
        self.score = score
        self.queue = queue


class ProxyWeight:
    def __init__(self, queue, weight):
        self.weight = weight
        self.queue = queue


class ProxyVerifiedTime:
    def __init__(self, queue, verified_time):
        self.verified_time = verified_time
        self.queue = queue


class ProxySpeed:
    def __init__(self, queue, response_time):
        self.response_time = response_time
        self.queue = queue


class ProxyValidateItem(scrapy.Item):
    # url
    url = scrapy.Field()
    # increase
    incr = scrapy.Field()
    # proxy score
    score_dimension = scrapy.Field()
    # proxy weight
    weight_dimension = scrapy.Field()
    # proxy verified time
    verified_time_dimension = scrapy.Field()
    # proxy speed
    speed_dimension = scrapy.Field()

    def __str__(self):
        return json.dumps(dict(self), default=lambda o: o.__dict__, sort_keys=False, indent=4)
