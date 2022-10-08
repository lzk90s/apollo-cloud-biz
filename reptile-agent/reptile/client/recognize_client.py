import requests

from reptile.client import build_gateway_request_url, check_response
from reptile.util import json_util


def recognize_color_category(desc_list):
    url = build_gateway_request_url("/recognize/recognition/color_category")
    resp = requests.post(url, json=desc_list)
    check_response(url, None, resp)
    return json_util.json2dict(resp.text)


def recognize_clothing_size_category(desc_list):
    url = build_gateway_request_url("/recognize/recognition/clothing_size_category")
    resp = requests.post(url, json=desc_list)
    check_response(url, None, resp)
    return json_util.json2dict(resp.text)


def recognize_age_category(desc_list):
    url = build_gateway_request_url("/recognize/recognition/age_category")
    resp = requests.post(url, json=desc_list)
    check_response(url, None, resp)
    return json_util.json2dict(resp.text)


def __ocr_result_contain_zh(ocr_result):
    for r in ocr_result:
        if r["iz_zh"]:
            return True
    return False


def is_image_contain_zh(image_url_list):
    '''
    判断图片是否包含中文
    :param image_url_list:
    :return:
    '''
    url = build_gateway_request_url("/recognize/recognition/image_ocr")
    resp = requests.post(url, json=image_url_list)
    check_response(url, None, resp)
    result = json_util.json2dict(resp.text)

    zh_result = []
    for item in result:
        zh_result.append(__ocr_result_contain_zh(item))
    return zh_result
