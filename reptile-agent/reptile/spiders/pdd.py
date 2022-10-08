# -*- coding: utf-8 -*-
import asyncio
import random
import time

from pyppeteer.network_manager import Response

from gerapy_pyppeteer import PyppeteerRequest
from reptile.client import product_client
from reptile.items import ProductItem
from reptile.spiders.base import BaseProductSpider
from reptile.util import json_util, trace_util

base_url = "https://mobile.yangkeduo.com"


class PddSpider(BaseProductSpider):
    name = 'pdd'
    captcha_failed_count = 0

    custom_settings = {
        'GERAPY_ENABLE_REQUEST_INTERCEPTION': True
    }

    def parse(self, response, **kwargs):
        pass

    def start_requests(self):
        async def goods_list_interceptor(page, request, pu_response):
            assert isinstance(request, PyppeteerRequest)
            assert isinstance(pu_response, Response)
            if pu_response.status in [301, 302]:
                self.logger.error("Cookies has expired")
                return None
            if pu_response.url.find("subfenlei_gyl_label") > 0:
                assert pu_response.status == 200
                res_json = await pu_response.json()
                meta = request.meta
                meta["goods_list"].extend(res_json["goods_list"])

        async def scroll_list_page(request, page):
            traces = trace_util.page_scroll_trace(random.randrange(50000, 70000), st=10)
            for t in traces:
                x = random.randrange(800, 900)
                y = t
                await page.evaluate("()=>{window.scrollTo(" + str(x) + "," + str(y) + ")}")
                time.sleep(0.1)
            await asyncio.sleep(2)

        yield PyppeteerRequest(url=self.rule_opts["url"],
                               callback=self.parse_goods_list,
                               wait_for=2,
                               meta={"goods_list": []},
                               pre_page_hooks=[self.add_cookies],
                               response_interceptor_hooks=[goods_list_interceptor],
                               post_page_hooks=[scroll_list_page, self.check_captcha, self.update_cookies])

    def parse_goods_list(self, response):
        goods_list = response.meta["goods_list"] or []
        urls = []
        for goods in goods_list:
            goods_id = str(goods["goods_id"])
            goods_name = str(goods["goods_name"])

            # 限制价格区间
            price = None
            if "group" in goods:
                price = float(goods["group"]["price_info"])
            if "price" in goods:
                price = float(goods["price"]) / 100
            if not price or price < float(self.link["min_price"]) or price > float(self.link["max_price"]):
                self.logger.info("Price {} filter out".format(price))
                continue

            # 过滤名称
            ignore_keywords = self.link["ignore_keywords"]
            for word in ignore_keywords:
                if word in goods_name:
                    self.logger.info("Keywords {} filter out, goods name is {}".format(word, goods_name))
                    continue

            # 判断是否已经上传
            if product_client.product_exist(str(goods_id)):
                self.logger.warning("Goods {} already uploaded".format(goods_id))
                continue
            detail_url = base_url + "/" + goods["link_url"]
            urls.append(detail_url)

        self.logger.info("Found {} goods".format(len(urls)))

        random.shuffle(urls)
        for url in urls:
            async def scroll_detail_page(request, page):
                traces = trace_util.page_scroll_trace(random.randrange(3200, 5000), st=10)
                for t in traces:
                    x = random.randrange(800, 900)
                    y = t
                    await page.evaluate("()=>{window.scrollTo(" + str(x) + "," + str(y) + ")}")
                    time.sleep(0.1)
                await asyncio.sleep(2)

            yield PyppeteerRequest(url=url,
                                   callback=self.parse_goods_detail,
                                   wait_for=2,
                                   pre_page_hooks=[self.add_cookies],
                                   post_page_hooks=[scroll_detail_page, self.check_captcha, self.update_cookies])

    def parse_goods_detail(self, response):
        if self.has_blocked(response):
            self.close_spider("blocked")
            return None

        if self.has_captcha(response):
            self.logger.warning("found captcha, retry url {}".format(response.login_home_url))
            return None

        raw_data = json_util.json2dict(self.parse_raw_data_json(response.text))
        if not raw_data:
            self.logger.error("No raw data found for url {}, raw data is {}".format(response.login_home_url, raw_data))
            return None

        goods_obj = raw_data["store"]["initDataObj"]["goods"]
        if not goods_obj:
            self.logger.error("Invalid goods obj, raw data is {}".format(raw_data))
            return None

        goods_id = goods_obj["goodsID"]
        title = goods_obj["goodsName"]

        properties = []
        for prop in goods_obj["goodsProperty"]:
            key = prop["key"]
            values = ", ".join(list(prop["values"]))
            properties.append(key + ": " + values)
        goods_desc = "\n".join(properties)

        images = [url for url in goods_obj["viewImageData"]]
        # max 20 images
        images = images[0:min(20, len(images))]
        main_image = images[0]

        # sku-config
        sku_list = []
        for sku_tag in goods_obj["skus"]:
            # name
            sku_image = sku_tag["thumbUrl"]
            if not sku_image:
                continue

            sku_id = sku_tag["skuId"]
            # price
            sku_price = sku_tag["groupPrice"]
            # count
            sku_count = sku_tag["quantity"]
            # specs
            sku_color = ""
            sku_size = ""
            specs_list = [spec["spec_value"] for spec in sku_tag["specs"]]
            if len(specs_list) >= 1:
                sku_color = specs_list[0]
            if len(specs_list) >= 2:
                sku_size = specs_list[1]

            attrs = {}
            if sku_color:
                attrs["color"] = sku_color
            if sku_size:
                attrs["size"] = sku_size
            sku = {
                "skuId": str(sku_id),
                "name": sku_color or sku_size,
                "price": sku_price,
                "storage": sku_count,
                "imageUrl": sku_image,
                "priceUnit": "rmb",
                "skuFeature": json_util.obj2json(attrs, indent=None)
            }
            sku_list.append(sku)

        if not sku_list:
            self.logger.warning("No sku found for goods {}, url is {}".format(goods_id, response.login_home_url))
            return None

        if len(sku_list) > 40:
            self.logger.warning("Too much sku for goods {}".format(goods_id))
            return None

        item = ProductItem()
        item["id"] = goods_id
        item["subject"] = title
        item["platform"] = self.name
        item["category"] = self.link["category"]
        item["description"] = goods_desc
        item["language"] = "zh_CN"
        item["detailUrl"] = response.login_home_url.strip()
        item["mainImageUrl"] = main_image
        item["extraImageUrls"] = images
        item["skuList"] = sku_list
        yield item

    async def add_cookies(self, request, page):
        request.cookies = self.cookies

    async def update_cookies(self, request, page):
        self.cookies = await page.cookies()

    async def check_captcha(self, request, page):
        pass

    @staticmethod
    def has_blocked(response):
        block_keyword = "原商品已售罄"
        if response.text.find(block_keyword) > 0:
            self.logger.error("Pdd has been blocked")
            return True
        return False

    @staticmethod
    def has_captcha(response):
        if response.status in [301, 302]:
            self.logger.error("Cookies has expired")
            return True
        return False

    def close_spider(self, reason):
        self.crawler.engine.close_spider(self, reason)

    @staticmethod
    def cookies_string2dict(cookies):
        item_dict = {}
        items = cookies.split(';')
        for item in items:
            arr = item.split('=')
            if len(arr) != 2:
                continue
            key = arr[0].replace(' ', '')
            value = arr[1]
            item_dict[key] = value
        return item_dict

    def parse_raw_data_json(self, text):
        raw_data_pattern = "window.rawData="
        text = str(text)
        start_idx = text.find(raw_data_pattern)
        if start_idx <= 0:
            return None
        start_idx = start_idx + len(raw_data_pattern)
        end_idx = text.find("\n", start_idx)
        if end_idx <= 0:
            return None
        if text[end_idx - 1] == ';':
            end_idx = end_idx - 1
        return text[start_idx:end_idx]
