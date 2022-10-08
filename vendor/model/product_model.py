import copy

from util import json_util


class UploadStatus:
    def __init__(self, status, message, details):
        self.status = status
        self.message = message
        self.details = details


class ProductSkuDTO:
    gtin = None
    skuId = None
    imageUrl = None
    attrs = None
    price = None
    shippingFee = None
    priceUnit = None
    count = None
    weight = ''


class ProductDTO:
    goodsId = None
    platform = None
    category = None
    subject = None
    description = None
    brand = None
    detailUrl = None
    mainImageUrl = None
    extraImageUrls = None
    feature = None
    skuList = None


class FlatProductInfo(ProductDTO, ProductSkuDTO):
    @staticmethod
    def conv2flat(goods_dict):
        assert isinstance(goods_dict, dict)
        obj_list = []
        for sku in goods_dict['skuList']:
            obj = FlatProductInfo()
            for k, v in goods_dict.items():
                if 'skuList' == k:
                    continue
                obj.__dict__[k] = copy.deepcopy(v)
            for k, v in sku.items():
                obj.__dict__[k] = copy.deepcopy(v)
            obj_list.append(obj)
            # 填充id
            obj.goodsId = goods_dict['id']
            obj.skuId = sku['skuId']
        return obj_list


if __name__ == "__main__":
    t = {
        'id': '10',
        'name': 'sdfsfs',
        'skuList': [
            {
                'price': 20,
                'imageUrl': 'fjslfs',
                'tt': 'ss'
            },
            {
                'price': 30,
                'imageUrl': 'tttttt',
                'tt': '2222s'
            }
        ]
    }
    aa = FlatProductInfo.from_goods_dto(t)
    print(json_util.obj2json(aa))
