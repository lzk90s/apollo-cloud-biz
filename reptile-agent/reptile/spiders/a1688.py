# -*- coding: utf-8 -*-
import asyncio
import math
import random
import re
import time

from bs4 import BeautifulSoup

from gerapy_pyppeteer import PyppeteerRequest
from reptile.client import product_client
from reptile.items import ProductItem
from reptile.spiders.base import BaseProductSpider
from reptile.util import json_util, trace_util, encrypt_util


class A1688Spider(BaseProductSpider):
    name = "1688"
    captcha_failed_count = 0

    def __del__(self):
        # cookie_fetcher.instance.save_cookies(self.name, self.cookies)
        # self.logger.info("Save cookies, cookies {}".format(self.cookies))
        pass

    def parse(self, response, **kwargs):
        pass

    def start_requests(self):
        url = self.rule_opts["url"]
        # url = "https://detail.1688.com/offer/645201450185.html"
        callback = self.parse_category_goods_result
        if "company/company_search.htm" in url:
            callback = self.parse_search_factory_result
        if "offerlist.htm" in url:
            callback = self.parse_factory_offer_result
        if "detail.1688.com" in url:
            callback = self.parse_goods_detail

        yield PyppeteerRequest(url=url,
                               callback=callback,
                               wait_for=2,
                               pre_page_hooks=[self.add_cookies],
                               post_page_hooks=[
                                   self.random_scroll_page,
                                   self.random_move_mouse,
                                   self.check_captcha,
                                   self.update_cookies
                               ])

    def parse_search_factory_result(self, response):
        """
        解析搜索工厂结果
        :param response:
        :return:
        """
        soup = BeautifulSoup(response.text, "lxml")
        company_tag_list = soup.find_all("div", attrs={"class": "company-info-contain"})
        if not company_tag_list:
            self.logger.info("No company found, url {}".format(response.login_home_url))
            return None

        # 解析工厂信息
        company_infos = []
        for company in company_tag_list:
            title_container = company.find("div", attrs={"class": "title-container"})
            if not title_container:
                continue
            url = title_container.a.attrs['href']
            name = title_container.a.string
            company_infos.append({"name": name, "url": url + "/page/offerlist.htm"})

        # 随机取1个工厂发起商品页请求
        company = random.choice(company_infos)
        self.logger.info("Request factory {}, url {}".format(company['name'], company['url']))
        yield PyppeteerRequest(url=company['url'],
                               callback=self.parse_factory_offer_result,
                               wait_for=2,
                               pre_page_hooks=[self.add_cookies],
                               post_page_hooks=[
                                   self.random_move_mouse,
                                   self.check_captcha,
                                   self.update_cookies
                               ])

    def parse_factory_offer_result(self, response):
        """
        解析工厂offer list 结果
        :param response:
        :return:
        """
        soup = BeautifulSoup(response.text, "lxml")
        page_count_tag = soup.find("em", attrs={"class": "page-count"})
        if not page_count_tag:
            self.logger.warning("No page count found")
            return None
        page_count = int(page_count_tag.string)
        offer_list_url_tag = soup.find("a", attrs={"class": "show-category"})
        if not offer_list_url_tag:
            self.logger.warning("No offer list found")
            return None
        offer_list_url = offer_list_url_tag.attrs['href']

        self.logger.info("Offer list url is {}, page count {}".format(offer_list_url, page_count))

        # 随机取4个分页
        page_count = min(4, page_count)
        page_list = [i for i in range(1, page_count + 1)]

        self.logger.info("Random page list {}".format(page_list))

        # 解析每个分页的商品
        for i in page_list:
            url = offer_list_url + "?pageNum=" + str(i)
            yield PyppeteerRequest(url=url,
                                   callback=self.parse_factory_goods_list,
                                   wait_for=2,
                                   pre_page_hooks=[self.add_cookies],
                                   post_page_hooks=[
                                       self.random_move_mouse,
                                       self.check_captcha,
                                       self.update_cookies
                                   ])

    def parse_factory_goods_list(self, response):
        """
        解析工厂商品列表
        :param response:
        :return:
        """
        soup = BeautifulSoup(response.text, "lxml")
        offer_row_tag_list = soup.find_all("li", attrs={"class": "offer-list-row-offer", "data-offerid": True})
        if not offer_row_tag_list:
            return None

        urls = []
        goods_id = None
        for offer in offer_row_tag_list:
            try:
                goods_id = offer.attrs['data-offerid']
                title_link_tag = offer.find("a", attrs={"class": "title-link"})
                goods_name = title_link_tag.attrs['title']
                # 限制价格区间
                offer_price_tag = offer.find("em", attrs={"class": "price-container"})
                if offer_price_tag:
                    price = float(offer_price_tag.string)
                    if price < float(self.link["minPrice"]) or price > float(self.link["maxPrice"]):
                        self.logger.info("Price {} filter out".format(price))
                        continue
                # 过滤名称
                ignore_keywords = self.link["ignoreKeywords"]
                for word in ignore_keywords:
                    if word in goods_name:
                        self.logger.info("Keywords {} filter out, goods name is {}".format(word, goods_name))
                        continue
                # 去重
                if product_client.product_exist(str(goods_id)):
                    self.logger.warning("Goods {} already uploaded".format(goods_id))
                    continue
                detail_url = title_link_tag.attrs['href']
                urls.append(detail_url)
            except Exception as e:
                self.logger.error("Process goods {} failed, {}".format(goods_id, e.args))

        # 保存历史
        product_client.save_history(encrypt_util.md5(response.login_home_url))

        # 请求商品详情列表
        return self.request_goods_detail_list(urls)

    def parse_category_goods_result(self, response):
        """
        解析分类商品结果
        :param response:
        :return:
        """
        soup = BeautifulSoup(response.text, "lxml")

        offer_list_container = soup.find("div", attrs={"class": "offerList"})
        if not offer_list_container:
            self.logger.error("No offer list found")
            return None

        urls = []
        goods_id = None
        offer_list = offer_list_container.find_all("div", attrs={"class": "cate1688-offer"})
        for offer_div in offer_list:
            try:
                goods_id = offer_div.attrs["id"]
                goods_name = offer_div.text
                # 限制价格区间
                offer_price_tag = offer_div.find("span", attrs={"class": "price-num"})
                if offer_price_tag:
                    price = float(offer_price_tag.span.string)
                    if price < float(self.link["min_price"]) or price > float(self.link["max_price"]):
                        self.logger.info("Price {} filter out".format(price))
                        continue
                # 过滤名称
                ignore_keywords = self.link["ignore_keywords"]
                for word in ignore_keywords:
                    if word in goods_name:
                        self.logger.info("Keywords {} filter out, goods name is {}".format(word, goods_name))
                        continue
                # 去重
                if product_client.product_exist(str(goods_id)):
                    self.logger.warning("Goods {} already uploaded".format(goods_id))
                    continue
                detail_url = str(offer_div.a.attrs["href"])
                urls.append(detail_url)
            except Exception as e:
                self.logger.error("Process goods {} failed, {}".format(goods_id, e.args))

        # 保存历史
        product_client.save_history(encrypt_util.md5(response.login_home_url))

        # 请求商品详情列表
        return self.request_goods_detail_list(urls)

    def request_goods_detail_list(self, urls):
        self.logger.info("Found {} goods".format(len(urls)))

        random.shuffle(urls)
        for url in urls:
            async def scroll_goods_detail_page(request, page):
                traces = trace_util.page_scroll_trace(random.randrange(1000, 2300), st=10)
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
                                   post_page_hooks=[
                                       scroll_goods_detail_page,
                                       self.random_move_mouse,
                                       self.check_captcha,
                                       self.update_cookies
                                   ])

    def parse_goods_detail(self, response):
        if self.has_captcha(response):
            self.logger.warning("found captcha, retry url {}".format(response.login_home_url))
            return None

        soup = BeautifulSoup(response.text, 'lxml')

        # 解析商品名称和id
        b2c_auction = soup.find("meta", attrs={"name": "b2c_auction"})
        og_title = soup.find("meta", attrs={"property": "og:title"})
        if not b2c_auction or not og_title:
            return None
        goods_id = b2c_auction.attrs["content"]
        title = str(og_title.attrs["content"])
        if '【' in title and '】' in title:
            # 去掉标题里面的特殊符号内的信息
            title = re.sub(r'【\w+】', '', title)

        # 解析图片
        images = []
        main_image = ''
        for image_tag in soup.find_all("li", attrs={"class": "tab-trigger", "data-imgs": True}):
            img_obj = json_util.json2obj(image_tag.attrs["data-imgs"])
            images.append(img_obj.original)

        # 去除带中文的图片
        is_zh_result = product_client.is_image_contain_zh(images)
        tmp_images = [images[i] for i in range(0, len(images)) if not is_zh_result[i]]
        images = tmp_images

        if not images:
            self.logger.warning("No images for goods {}".format(goods_id))
            return None

        main_image = images[0]

        # 解析商品feature
        feature_dict = {}
        attrs_tag = soup.find("div", attrs={"class": "mod-detail-attributes", "data-feature-json": True})
        if attrs_tag:
            filter_out_tags = ['发货时间', '下游平台', '主要下游平台', '主要销售地区', '是否跨境出口专供货源']
            data_feature_list = json_util.json2dict(attrs_tag.attrs["data-feature-json"])
            for a in data_feature_list:
                if a['name'] in filter_out_tags:
                    continue
                feature_dict[a['name']] = a['value']

        # 解析feature
        brand = age_category = sex_category = None
        if '品牌' in feature_dict:
            brand = feature_dict['品牌']
        if '适合年龄段' in feature_dict:
            age_category = feature_dict['适合年龄段']
            result_list = product_client.recognize_age_category([age_category])
            age_category = result_list[0] if result_list else None
        if '适用性别' in feature_dict:
            sex = feature_dict['适用性别']
            if '男' in sex:
                sex_category = 'Male'
            elif '女' in sex:
                sex_category = 'Female'
            else:
                sex_category = 'Unisex'

        # 解析跨境包裹重量
        weight = None
        kuajing_attr = soup.find("div", attrs={"class": "kuajing-attribues"})
        if kuajing_attr:
            weight = kuajing_attr.dl.dd.span.em.string

        # 解析sku信息
        sku_props = re.findall(r"skuProps:(.*?),\n", response.text)
        sku_map = re.findall(r"skuMap:(.*?),\n", response.text)
        if not sku_props or not sku_map:
            self.logger.warning("No sku map found, url {}".format(response.login_home_url))
            return None
        sku_props_obj = json_util.json2dict(sku_props[0])
        sku_map_obj = json_util.json2dict(sku_map[0])

        # 解析sku prop
        color_image_map = {}
        clothing_size_map = {}
        color_desc_list = []
        clothing_size_desc_list = []
        for sku_prop in sku_props_obj:
            if '颜色' in sku_prop['prop']:
                for item in sku_prop['value']:
                    if 'imageUrl' not in item:
                        continue
                    color_image_map[item['name']] = item['imageUrl']
                    color_desc_list.append(item['name'])
            elif '身高' in sku_prop['prop']:
                for item in sku_prop['value']:
                    clothing_size_map[item['name']] = item['name']
                    clothing_size_desc_list.append(item['name'])

        # 识别分类
        color_category_map = {}
        clothing_size_category_map = {}
        if color_desc_list:
            result_list = product_client.recognize_color_category(color_desc_list)
            for i in range(len(color_desc_list)):
                color_category_map[color_desc_list[i]] = result_list[i]
        if clothing_size_desc_list:
            result_list = product_client.recognize_clothing_size_category(clothing_size_desc_list)
            for i in range(len(clothing_size_desc_list)):
                clothing_size_category_map[clothing_size_desc_list[i]] = result_list[i]

        # 解析sku map
        sku_list = []
        ref_price = re.findall(r"refPrice:(.*?),\n", response.text)
        ref_price = float(str(ref_price[0]).strip()[1:-1]) if ref_price else None
        for sku in sku_map_obj:
            info = sku_map_obj[sku]
            sku_full_name = str(sku).replace('&gt;', '>')
            sku_id = info['skuId']
            sku_price = float(info['discountPrice']) if 'discountPrice' in info else ref_price
            sku_count = int(info['canBookCount'])

            sku_color = sku_size = sku_image = sku_color_category = sku_size_category = None
            for name in sku_full_name.split('>'):
                if name in color_category_map:
                    sku_color = name
                    sku_color_category = color_category_map[sku_color]
                    sku_image = color_image_map[sku_color] if sku_color in color_image_map else None
                if name in clothing_size_map:
                    sku_size = name
                    sku_size_category = clothing_size_category_map[sku_size]
                    # 小于30说明大概率是有问题的
                    if sku_size_category and int(sku_size_category) < 30:
                        sku_size_category = None

            # 如果sku库存为0，表示没有库存，忽略sku
            if sku_count == 0:
                self.logger.warning("Sku {} storage is 0, ignore".format(sku_id))
                continue
            # 如果sku没有图片，忽略sku
            if not sku_image:
                self.logger.warning("Sku {} no image, ignore".format(sku_id))
                continue
            # 如果存在sku color，但是没有识别到sku color分类，表示分类是不标准的，忽略
            if sku_color and not sku_color_category:
                self.logger.warning("Sku color category not recognized, color {}, ignore".format(sku_color))
                continue
            # 如果存在sku size，但是没有识别到sku size分类，表示分类是不标准的，忽略
            if sku_size and not sku_size_category:
                self.logger.warning("Sku size category not recognized, size {}, ignore".format(sku_size))
                continue
            # 如果sku图片中带中文，忽略sku
            if product_client.is_image_contain_zh([sku_image])[0]:
                self.logger.warning("Sku image {} contain zh, ignore".format(sku_image))
                continue

            # 生成attr字段
            attrs = {}
            if sku_color:
                attrs["color"] = sku_color
            if sku_color_category:
                attrs["colorCategory"] = sku_color_category
            if sku_size_category:
                attrs["clothingSize"] = sku_size_category
            if age_category:
                attrs["ageCategory"] = age_category
            if sex_category:
                attrs["sexCategory"] = sex_category

            sku = {
                "skuId": sku_id,
                "name": sku_full_name,
                "price": sku_price,
                "storage": sku_count,
                "imageUrl": sku_image,
                "priceUnit": "rmb",
                "skuFeature": attrs,
                "weight": weight
            }
            sku_list.append(sku)

        if not sku_list:
            self.logger.warning("No sku found for goods {}, url is {}".format(goods_id, response.login_home_url))
            return None

        item = ProductItem()
        item["id"] = goods_id
        item["subject"] = title
        item["platform"] = self.name
        item["category"] = self.link["category"]
        item["description"] = ''
        item["brand"] = brand
        item["language"] = "zh_CN"
        item["detailUrl"] = response.login_home_url.strip()
        item["mainImageUrl"] = main_image
        item["extraImageUrls"] = images[1:-1]
        item["feature"] = feature_dict
        item["skuList"] = sku_list
        yield item

    async def add_cookies(self, request, page):
        request.cookies = self.cookies

    async def update_cookies(self, request, page):
        self.cookies = await page.cookies()

    async def random_scroll_page(self, request, page):
        traces = trace_util.page_scroll_trace(random.randrange(8000, 9000), st=10)
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

    @staticmethod
    def has_captcha(response):
        return response.text.find(r"页面-验证码") > 0

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
