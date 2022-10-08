import scrapy

from reptile.client import cookiespool_client
from reptile.util import json_util


class BaseSpider(scrapy.Spider):
    rule_id = None
    rule_opts = None
    use_cookies = True
    cookies = None

    def __init__(self, *args, **kwargs):
        super().__init__(kwargs=kwargs)
        self.load_rule(kwargs['rule_id'], kwargs['rule_opts'])
        if self.use_cookies:
            self.load_cookies()

    def parse(self, response, **kwargs):
        pass

    def load_rule(self, rule_id, rule_opts):
        # load links
        if not rule_id:
            raise ValueError("Invalid reptile rule id")
        if rule_opts:
            self.rule_opts = json_util.json2dict(rule_opts)
        else:
            self.rule_opts = {}
        self.rule_id = rule_id
        self.logger.info("load reptile rule {} succeed, rule_opts={}".format(self.rule_id, rule_opts))

    def load_cookies(self):
        # load cookies
        self.cookies = cookiespool_client.get_random_cookies(self.name)
        if not self.cookies:
            raise ValueError("Invalid cookies")
        self.logger.info("Load cookies succeed, cookies {}".format(self.cookies))


class BaseProductSpider(BaseSpider):
    name = None
    use_proxy = False
    proxy_task = None

    custom_settings = {
        'GERAPY_PYPPETEER_SLEEP': 7,
    }

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        if not self.proxy_task:
            self.proxy_task = self.name

    def get_reptile_rule_id(self):
        if not self.rule_id:
            raise Exception()
        return self.rule_id


class BaseStockSpider(BaseSpider):
    pass
