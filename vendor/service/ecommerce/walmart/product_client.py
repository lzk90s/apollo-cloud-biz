import logging
import os
import time

from common.exception import BizException
from model import product_model
from model.product_model import FlatProductInfo
from service.category import category_provider
from service.ecommerce.client import ProductClient, check_session
from service.ecommerce.walmart.item import builder
from service.ecommerce.walmart.item.base import MpFeedItemRequest, MpItem
from service.ecommerce.walmart.walmart_api import Walmart
from util import json_util


class WalmartProductClient(ProductClient):
    def __init__(self, client_id, client_secret, cookies):
        super().__init__(client_id, client_secret, cookies)
        if not os.getenv("WALMART_PROXY"):
            raise ValueError("WALMART_PROXY must set")
        self.proxies = {
            "http": os.getenv("WALMART_PROXY"),
            "https": os.getenv("WALMART_PROXY")
        }
        logging.info("Proxy is {}".format(self.proxies))
        self.walmart = Walmart(self.client_id, self.client_secret, self.proxies)
        self.expire_time = time.time() + self.walmart.token_expires_in
        logging.info("Token expire at {}".format(self.expire_time))

    def is_session_expired(self):
        t = time.time()
        return self.expire_time - t < 30

    def reload(self):
        logging.info("Reload client")
        self.walmart = Walmart(self.client_id, self.client_secret, self.proxies)

    @check_session
    def upload_item(self, products):
        logging.info('Upload item, products = {}'.format(json_util.obj2json(products)))
        req = self.build_item_request(products)
        rsp = self.walmart.feed.create('MP_ITEM', json_util.obj2json(req))
        logging.info("Upload item, req = {}".format(json_util.obj2json(req)))
        logging.info("Upload item, rsp = {}".format(json_util.obj2json(rsp)))
        errors = rsp['errors']
        if errors:
            raise BizException('Upload product failed, {}'.format(errors))
        feed_id = rsp['feedId']
        return feed_id

    @check_session
    def get_upload_status(self, upload_id):
        result = self.walmart.feed.get_status(upload_id)
        error_message = ''
        detail_list = []
        status = None

        logging.info("Upload status for {} is {}".format(upload_id, json_util.obj2json(result)))

        # 如果feed status不是已经处理完成，返回处理中
        feed_status = result['feedStatus']
        if 'PROCESSED' != feed_status:
            status = 'processing'
            return product_model.UploadStatus(status, None, None)

        received_num = result['itemsReceived']
        succeed_num = result['itemsSucceeded']
        failed_num = result['itemsFailed']

        # 转换状态
        if received_num == succeed_num:
            status = 'succeed'
        elif failed_num and failed_num > 0:
            error_message = "成功{}个，失败{}个。".format(succeed_num, failed_num)
            status = 'failed'

        # 生成详情
        item_list = result['itemDetails']['itemIngestionStatus']
        for item in item_list:
            sku = item['sku']
            s = item['ingestionStatus']
            succeed = False
            if s == 'INPROGRESS':
                message = "正在处理中"
            elif s == 'SUCCESS':
                succeed = True
                message = None
            elif s == 'TIMEOUT_ERROR':
                message = "处理超时"
            else:
                errs = item['ingestionErrors']['ingestionError']
                errs_list = ['[{}-{}] {}'.format(e['type'], e['field'], e['description']) for e in errs]
                message = "\n".join(errs_list)
            detail_list.append({'skuId': sku, 'succeed': succeed, 'message': message})

        return product_model.UploadStatus(status, error_message, detail_list)

    @check_session
    def enable_sale(self, goods_ids):
        pass

    @check_session
    def disable_sale(self, goods_ids):
        pass

    @check_session
    def delete_item(self, goods_ids):
        pass

    def new_walmart_client(self):
        return

    @staticmethod
    def convert_feed_status(status):
        if 'PROCESSED' == status:
            return 'succeed'
        return status

    @staticmethod
    def get_category_id_by_name(name):
        cat = category_provider.get_category_by_name('walmart', name)
        if not cat:
            raise BizException("No category {} found".format(name))
        return cat.id

    @staticmethod
    def build_item_request(sku_list):
        item_list = []
        category_name = sku_list[0].category
        for sku in sku_list:
            assert isinstance(sku, FlatProductInfo)
            visible_builder = builder.get_visible_builder(category_name)
            if not visible_builder:
                raise Exception("Unsupported")

            item = MpItem(
                gtin=sku.gtin,
                price=round(sku.price, 2),
                sku_id=sku.skuId,
                weight=sku.weight if sku.weight else 0.5,
                goods_name=sku.subject,
                brand=sku.brand if sku.brand else 'none',
                category_name=category_name,
                item_visible=visible_builder(sku)
            )
            item_list.append(item)
        return MpFeedItemRequest(WalmartProductClient.get_category_id_by_name(category_name), item_list)
