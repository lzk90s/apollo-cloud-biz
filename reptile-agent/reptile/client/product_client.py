import requests

from reptile.client import build_gateway_request_url, check_response


def upload_product(product):
    url = build_gateway_request_url("/product/internal/product")
    resp = requests.post(url, json=product)
    check_response(url, product, resp)


def product_exist(product_id):
    url = build_gateway_request_url("/product/internal/product/" + product_id + "/exists")
    resp = requests.get(url)
    check_response(url, None, resp)
    return 'true' == resp.text


def save_history(key):
    params = {
        "key": key
    }
    url = build_gateway_request_url("/product/internal/reptile/history")
    resp = requests.post(url, params=params)
    check_response(url, None, resp)


def history_exist(key):
    params = {
        "key": key
    }
    url = build_gateway_request_url("/product/internal/reptile/history")
    resp = requests.get(url, params=params)
    check_response(url, None, resp)
