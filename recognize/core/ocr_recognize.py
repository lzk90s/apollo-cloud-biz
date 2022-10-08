import base64

import requests

from util import json_util

_PADDLE_OCR_BASE_URL = "http://paddle-ocr:9000/"


def recognize(image_url):
    url = _PADDLE_OCR_BASE_URL + "/predict/ocr_system"
    headers = {"Content-Type": "application/json"}
    payload = {"images": [get_base64_image(image_url)]}
    res = requests.post(url, headers=headers, json=payload)
    if res.status_code != 200:
        raise Exception("Ocr recognize failed")
    result = json_util.json2dict(res.text)
    if not result["results"]:
        return None
    tmp_result = result["results"][0]
    for r in tmp_result:
        text = r["text"]
        if is_chinese(text):
            r["is_zh"] = True
        else:
            r["is_zh"] = False
    return tmp_result


def get_base64_image(url):
    res = requests.get(url)
    if res.status_code != 200:
        raise Exception("Get image failed")
    return base64.encodebytes(res.content).decode("utf-8")


def is_chinese(string):
    """
    检查整个字符串是否包含中文
    :param string: 需要检查的字符串
    :return: bool
    """
    for ch in string:
        if u'\u4e00' <= ch <= u'\u9fff':
            return True
    return False
