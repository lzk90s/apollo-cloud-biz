# -*- coding: utf-8 -*-
import asyncio
import math
import random
import time

from bs4 import BeautifulSoup

from gerapy_pyppeteer import PyppeteerRequest
from reptile.items import ProductItem
from reptile.spiders.base import BaseProductSpider
from reptile.util import json_util, trace_util


class WalmartSpider(BaseProductSpider):
    name = "walmart"
    captcha_failed_count = 0
    home_url = "https://www.walmart.com"
    use_proxy = False

    # proxy_task = "https"

    def start_requests(self):
        url = self.rule_opts['url']
        yield PyppeteerRequest(url=url,
                               callback=self.parse_goods_skus,
                               wait_for=2,
                               pre_page_hooks=[self.add_cookies],
                               post_page_hooks=[self.check_captcha, self.random_move_mouse])

    def parse_goods_skus(self, response):
        soup = BeautifulSoup(response.text, 'lxml')

        async def compare_all_seller(response, page):
            sellers_button = await page.querySelector("[aria-label='Compare all sellers']")
            if not sellers_button:
                self.logger.warning("No compare all selector button")
                return
            await page.click("[aria-label='Compare all sellers']")
            self.logger.info("Click compare all sellers button")

        sku_link_tags = soup.find_all("a", attrs={"link-identifier": "Generic Name", "class": None})
        if not sku_link_tags:
            return None

        for sku in sku_link_tags:
            # 拼接url
            url = self.home_url + sku.attrs["href"]
            if response.login_home_url in url:
                self.logger.info("Skip current sku, url {}".format(url))
                continue
            yield PyppeteerRequest(url=url,
                                   callback=self.parse_sku_detail,
                                   pre_page_hooks=[self.add_cookies],
                                   post_page_hooks=[self.check_captcha, self.random_move_mouse, compare_all_seller],
                                   wait_for=2)
        # 解析当前
        yield self.parse_sku_detail(response)

    def parse_sku_detail(self, response):
        soup = BeautifulSoup(response.text, 'lxml')

        json_content = soup.find('script', attrs={'type': 'application/ld+json'}).string
        if not json_content:
            self.logger.error("No json data")
            return None

        sku_info = json_util.json2dict(json_content)
        if not sku_info:
            self.logger.error("No sku info")
            return None

        offer_info = sku_info["offers"]
        if not offer_info:
            self.logger.error("No offer")
            return None

        offer_list = soup.find_all("div", attrs={"data-testid": "allSellersOfferLine"})
        if not offer_list:
            self.logger.error("No allSellersOfferLine, ignore this sku")
            return None

        follow_sellers = []
        for offer in offer_list:
            price_str = offer.div.div.div.string
            if not price_str:
                self.logger.warning("No price")
                continue
            seller_link_tag = offer.find("a", attrs={"data-testid": "seller-name-link"})
            if not seller_link_tag:
                continue
            price = price_str[1:]
            seller = seller_link_tag.string.string
            follow_sellers.append({'seller': seller, 'price': price, 'priceUnit': 'dollar'})

        sku = {
            "skuId": sku_info["gtin13"],
            "name": sku_info["gtin13"],
            "price": offer_info["price"],
            "storage": 999,
            "imageUrl": sku_info["image"],
            "priceUnit": "dollar",
            "skuFeature": {},
            "weight": 10
        }

        item = ProductItem()
        item["id"] = sku_info["gtin13"]
        item["reptileRuleId"] = self.get_reptile_rule_id()
        item["subject"] = sku_info["name"]
        item["platform"] = self.name
        item["category"] = self.rule_opts["category"]
        item["description"] = sku_info["description"]
        item["brand"] = sku_info["brand"]["name"]
        item["language"] = "en_US"
        item["detailUrl"] = response.login_home_url.strip()
        item["mainImageUrl"] = sku_info["image"]
        item["extraImageUrls"] = []
        item["feature"] = None
        item["skuList"] = [sku]
        yield item

    async def add_cookies(self, request, page):
        request.cookies = self.cookies

    async def update_cookies(self, request, page):
        self.cookies = await page.cookies()

    async def random_scroll_page(self, request, page):
        traces = trace_util.page_scroll_trace(random.randrange(300, 600), st=10)
        for t in traces:
            x = random.randrange(200, 900)
            y = t
            await page.evaluate("()=>{window.scrollTo(" + str(x) + "," + str(y) + ")}")
            time.sleep(0.1)
        await asyncio.sleep(2)

    async def random_move_mouse(self, request, page):
        mouse = page.mouse
        viewport = page.viewport
        max_x = viewport['width']
        max_y = viewport['height']
        num = 10
        # 随机生成一堆点
        x_list = [random.randint(10, max_x) for i in range(0, num)]
        y_list = [random.randint(10, max_y) for i in range(0, num)]
        point_list = [(x_list[i], y_list[i]) for i in range(0, num)]
        self.logger.info("Random move, {}".format(point_list))
        # 移动鼠标
        for p in point_list:
            await mouse.move(p[0], p[1])
            await asyncio.sleep(0.1)

    async def check_captcha(self, request, page):
        original_page = page
        iframe = await page.querySelector('iframe')
        if iframe:
            page = await iframe.contentFrame()

        # 没有验证码直接返回
        slider = await page.querySelector("#nc_1_n1t")
        if not slider and not await page.querySelector("#nocaptcha"):
            return None

        max_retry = 7
        retry = 1
        succeed = False
        while retry <= max_retry:
            # 获取验证码div
            slider = await page.querySelector("#nc_1_n1t")
            if slider:
                rect = await slider.boundingBox()
                # await page.hover(captcha_id)
                # 滑动滑块
                x = rect["x"]
                y = rect["y"]
                w = rect["width"]
                h = rect["height"]
                x = x + 10
                y = y + math.floor(h / 2)
                mouse = original_page.mouse
                await mouse.move(x, y)
                await mouse.down()
                self.logger.info("Slice {} times".format(retry))
                traces = trace_util.captcha_slice_trace(w)
                begin_time = time.time()
                diff_time = 0
                for s in traces:
                    tm = float(s[0])
                    tx = float(s[1])
                    ty = float(s[2])
                    # 等待时间到达
                    while diff_time < tm:
                        diff_time = time.time() - begin_time
                        continue
                    _y = random.randrange(-5, 5)
                    await mouse.move(x + tx, y + ty + _y)
                await mouse.up()
                retry = retry + 1

            # 判断验证是否成功
            retry_again = await page.querySelector('.nc_iconfont.icon_warn')
            slider_again = await page.querySelector('.nc_iconfont.btn_slide')
            captcha_tips = await page.querySelector('.captcha-tips')
            if retry_again is None and slider_again is None and captcha_tips is None:
                succeed = True
                break
            # 重新加载页面，继续下一次验证
            await page.reload()
            await asyncio.sleep(1)

        # 如果没有成功，判断验证码是不是失败太多，如果失败次数太多，就停止爬取
        if succeed:
            await page.reload()
            await self.update_cookies(request, page)
            self.logger.info("Captcha verify succeed")
            self.captcha_failed_count = 0
        else:
            self.logger.info("Captcha verify failed")
            self.captcha_failed_count = self.captcha_failed_count + 1
            if self.captcha_failed_count >= 2:
                self.logger.info("Too much failed for captcha check, close the spider")
                self.close_spider("Too much failed for captcha check, close the spider")

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
