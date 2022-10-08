from service.ecommerce.walmart.item.base import BaseItemVisible


class ClothingVisible(BaseItemVisible):
    ageGroup = None
    clothingSize = None
    clothingSizeGroup = None
    additionalProductAttributes = None

    def __init__(self, group_id, goods_description, main_image_url, extra_image_urls, features, color, color_category,
                 age_category, clothing_size):
        super().__init__()
        self.mainImageUrl = main_image_url
        self.shortDescription = goods_description
        self.productSecondaryImageURL = extra_image_urls
        self.keyFeatures = features
        self.color = str(color).split(",")
        self.colorCategory = str(color_category).split(",")
        self.variantGroupId = group_id
        self.variantAttributeNames = ['color', 'clothingSize']
        self.isPrimaryVariant = 'No'
        self.ageGroup = str(age_category).split(",")
        self.clothingSize = clothing_size


SIZE_CONVERT_MAP = {
    "50": "0-4months",
    "60": "0-6months",
    "70": "6-9months",
    "80": "9-12months",
    "90": "2T",
    "100": "3T",
    "110": "4T",
    "120": "5T",
    "130": "6T",
    "140": "7T",
    "150": "8T",
    "160": "9T",
    "170": "10T",
    "180": "11T",
    "190": "12T",
    "200": "13T"
}


def convert_clothing_size(size):
    if size not in SIZE_CONVERT_MAP:
        return None
    return SIZE_CONVERT_MAP[size]


def build_visible_from_sku(sku):
    color = sku.skuFeature['color'] if 'color' in sku.skuFeature else ''
    color_category = sku.skuFeature['colorCategory'] if 'colorCategory' in sku.skuFeature else None
    age_category = sku.skuFeature['ageCategory'] if 'ageCategory' in sku.skuFeature else None
    clothing_size = sku.skuFeature['clothingSize'] if 'clothingSize' in sku.skuFeature else None
    # 获取关键feature
    features = []
    for i in range(1, 10):
        f_key = "keyFeature" + str(i)
        if f_key in sku.feature:
            features.append(sku.feature[f_key])

    return ClothingVisible(
        group_id=sku.goodsId,
        goods_description=sku.description,
        main_image_url=sku.imageUrl,
        extra_image_urls=sku.extraImageUrls,
        features=features,
        color=color,
        color_category=color_category,
        age_category=age_category,
        clothing_size=convert_clothing_size(clothing_size)
    )
