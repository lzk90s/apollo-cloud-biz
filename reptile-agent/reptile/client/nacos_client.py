import random

import requests

from reptile.client import build_gateway_request_url, check_response
from reptile.util import json_util


def get_config(data_id, group="DEFAULT_GROUP"):
    payload = {
        "dataId": data_id,
        "group": group
    }
    url = build_gateway_request_url("/config/nacos/v1/cs/configs")
    resp = requests.get(url, params=payload)
    check_response(url, payload, resp)
    return json_util.json2dict(resp.text)


_CAPTCHA_SLICE_TRACE_LIST_CACHE = None


def get_captcha_slice_trace():
    global _CAPTCHA_SLICE_TRACE_LIST_CACHE
    if not _CAPTCHA_SLICE_TRACE_LIST_CACHE:
        _CAPTCHA_SLICE_TRACE_LIST_CACHE = get_config("captcha_slice_trace")
    return random.choice(_CAPTCHA_SLICE_TRACE_LIST_CACHE)
