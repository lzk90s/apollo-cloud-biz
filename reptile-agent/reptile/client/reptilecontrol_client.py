import requests

from reptile.client import build_gateway_request_url, check_response
from reptile.util import json_util


def get_reptile_rule(spider_name) -> list:
    params = {
        "spider": spider_name
    }
    url = build_gateway_request_url("/reptile-control/internal/reptile_rule/by_spider")
    resp = requests.get(url, params=params)
    check_response(url, params, resp)
    return json_util.json2dict(resp.text)
