import os

from util import json_util, gtin_util
from walmart_api import *

proxies = {
    "http": os.getenv("WALMART_PROXY"),
    "https": os.getenv("WALMART_PROXY")
}


def feed():
    feed_content = {
        "MPItemFeedHeader": {
            "subCategory": "baby_food",
            "sellingChannel": "marketplace",
            "processMode": "REPLACE",
            "locale": "en",
            "version": "1.2",
            "subset": "EXTERNAL"
        },
        "MPItem": [
            {
                "Visible": {
                    "Baby Food": {
                        "shortDescription": "Site Description for the item",
                        "keyFeatures": [
                            "KidzStuff Baby Sun Protection Bodysuit"
                        ],
                        "mainImageUrl": "https://i5-qa.walmartimages.com/asr/5a3e719f-3bca-4281-8333-030c5a2756bf.62d73c2f61f929ef580fe375e3597c13.jpeg"
                    }
                },
                "Orderable": {
                    "productIdentifiers": {
                        "productId": "06275872578689",
                        "productIdType": "GTIN"
                    },
                    "shipsInOriginalPackaging": "No",
                    "endDate": "2040-01-01T00:00:00.000Z",
                    "price": 12,
                    "sku": "skuAutomation-1627586740349",
                    "ShippingWeight": 2,
                    "brand": "Kidzstuff",
                    "MustShipAlone": "No",
                    "productName": "KidzStuff Baby Sun Protection Bodysuit",
                    "startDate": "2021-07-29T00:00:00.000Z"
                }
            }
        ]
    }

    gtin1 = gtin_util.generate_gtin()
    gtin2 = gtin_util.generate_gtin()
    sku1 = gtin1
    sku2 = gtin2
    group_id = 'testgroup'

    json_data = {
        "MPItemFeedHeader": {
            "locale": "en",
            "processMode": "REPLACE",
            "sellingChannel": "marketplace",
            "subCategory": "clothing_other",
            "subset": "EXTERNAL",
            "version": "1.2"
        },
        "MPItem": [
            {
                "Visible": {
                    "Clothing": {
                        "keyFeatures": [
                            "KidzStuff Baby Sun Protection Bodysuit"
                        ],
                        "mainImageUrl": "https://cbu01.alicdn.com/img/ibank/O1CN01oMkqJ020yx4TsFubw_!!2206541166919-0-cib.jpg",
                        "shortDescription": "100% Cotton\n\nPull On closure\n\nMachine Wash\n\nComfy feeling:Breathable and soft cotton long sleeve pajamas for boys ensure comfortable natural night sleep,is also good for children skin\n\nMatching sleep set:\nBoy crewneck solar system shirt,pants with elasticized waistband.The snugly fitted pjs suited to 2-10 yrs boys,recommend one or two bigger size because of the snug fit\n\nImaginative style:Super cool planet pajamas set,full of childlike which will be a great surprise for little boys as xmas gift or any others festival gifts",
                        "variantGroupId": group_id,
                        "variantAttributeNames": [
                            "color",
                            "clothingSize"
                        ],
                        "isPrimaryVariant": "No",
                        "color": ['red'],
                        "clothingSize": "L"
                                        "",
                        "clothingSizeGroup": "Toddler"
                    }
                },
                "Orderable": {
                    "MustShipAlone": "No",
                    "ShippingWeight": 10.0,
                    "price": 10.46,
                    "productIdentifiers": {
                        "productId": gtin1,
                        "productIdType": "GTIN"
                    },
                    "productName": "Baby long underwear suit combed cotton high waist belly protection underwear set children pajamas new autumn pajamas",
                    "shipsInOriginalPackaging": "No",
                    "sku": sku1,
                    "brand": "nancy"
                }
            },
            {

                "Visible": {
                    "Clothing": {
                        "keyFeatures": [
                            "KidzStuff Baby Sun Protection Bodysuit"
                        ],
                        "mainImageUrl": "https://t00img.yangkeduo.com/goods/images/2020-03-29/e4a9e8ac-d61c-4223-b976-586c0aaf058c.jpg",
                        "shortDescription": "100% Cotton\n\nPull On closure\n\nMachine Wash\n\nComfy feeling:Breathable and soft cotton long sleeve pajamas for boys ensure comfortable natural night sleep,is also good for children skin\n\nMatching sleep set:\nBoy crewneck solar system shirt,pants with elasticized waistband.The snugly fitted pjs suited to 2-10 yrs boys,recommend one or two bigger size because of the snug fit\n\nImaginative style:Super cool planet pajamas set,full of childlike which will be a great surprise for little boys as xmas gift or any others festival gifts",
                        "variantGroupId": group_id,
                        "variantAttributeNames": [
                            "color",
                            "clothingSize"
                        ],
                        "isPrimaryVariant": "No",
                        "color": ['black'],
                        "clothingSize": "M",
                        "clothingSizeGroup": "Toddler"
                    }
                },
                "Orderable": {
                    "MustShipAlone": "No",
                    "ShippingWeight": 200.0,
                    "brand": "nancy",
                    "price": 10.46,
                    "productIdentifiers": {
                        "productId": gtin2,
                        "productIdType": "GTIN"
                    },
                    "productName": "Baby long underwear suit combed cotton high waist belly protection underwear set children pajamas new autumn pajamas",
                    "shipsInOriginalPackaging": "No",
                    "sku": sku2
                }
            }
        ]
    }

    walmart = Walmart(os.getenv("CLIENT_ID"), os.getenv("CLIENT_SEC"), proxies)
    print(json_util.obj2json(walmart.feed.create("MP_ITEM", json_util.obj2json(json_data))))
    pass


def get_feed_status():
    walmart = Walmart(os.getenv("CLIENT_ID"), os.getenv("CLIENT_SEC"), proxies)
    print(json_util.obj2json(walmart.feed.all()))
    pass


def get_categories():
    walmart = Walmart(os.getenv("CLIENT_ID"), os.getenv("CLIENT_SEC"), proxies)
    cc = walmart.category.all()
    payload_list = cc["payload"]
    cat_list = []
    for payload in payload_list:
        id = payload['categoryId']
        name = payload['categoryName']
        cat = {
            "name": name,
            "id": id,
            "description": "",
            "url": ""
        }
        cat_list.append(cat)
    print(json_util.obj2json(cat_list))
    pass


if __name__ == "__main__":
    feed()
