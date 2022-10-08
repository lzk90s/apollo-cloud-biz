import re


class MpFeedItemRequest:
    def __init__(self, sub_category, item_list):
        self.MPItemFeedHeader = {
            'subCategory': sub_category,
            'sellingChannel': 'marketplace',
            'version': '1.2',
            'processMode': 'REPLACE',
            'locale': 'en',
            'subset': 'EXTERNAL'
        }
        self.MPItem = item_list


def parse_num_from_str(desc):
    # 提取字符串中的数字，返回最大的
    pattern = re.compile(r'\d+')
    res = re.findall(pattern, desc)
    res = list(map(int, res))
    res.sort(reverse=True)
    if res:
        return res[0]
    return None


class MpItem:
    def __init__(self, gtin, price, sku_id, weight, goods_name, brand, category_name, item_visible):
        self.Orderable = {
            'productIdentifiers': {
                'productId': gtin,
                'productIdType': 'GTIN'
            },
            'shipsInOriginalPackaging': 'No',
            'price': price,
            'sku': sku_id,
            'ShippingWeight': parse_num_from_str(weight),
            'brand': brand,
            'MustShipAlone': 'No',
            'productName': goods_name
        }

        self.Visible = {
            category_name: item_visible
        }


class BaseItemVisible:
    mainImageUrl = ''
    shortDescription = ''
    productSecondaryImageURL = []
    keyFeatures = []
    count = '1000'
    color = []
    colorCategory = []
    variantGroupId = ''
    variantAttributeNames = ['color']
    isPrimaryVariant = 'No'
