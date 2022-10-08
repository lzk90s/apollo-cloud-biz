import requests

from reptile.client import build_gateway_request_url, check_response
from reptile.util import json_util


def upload_stock_hq(stock):
    url = build_gateway_request_url("/stock/internal/stock_hq")
    resp = requests.post(url, json=stock if isinstance(stock, list) else [stock])
    check_response(url, stock, resp)


def upload_stock_basic(stock):
    url = build_gateway_request_url("/stock/internal/stock_basic")
    resp = requests.post(url, json=stock if isinstance(stock, list) else [stock])
    check_response(url, stock, resp)


def upload_stock_daily(stock):
    url = build_gateway_request_url("/stock/internal/stock_daily")
    resp = requests.post(url, json=stock if isinstance(stock, list) else [stock])
    check_response(url, stock, resp)


def get_all_stock_codes(maxTcap=None):
    params = {
        'maxTcap': maxTcap
    }
    url = build_gateway_request_url("/stock/internal/all_stock_codes")
    resp = requests.get(url, params=params)
    check_response(url, None, resp)
    return json_util.json2dict(resp.text)
