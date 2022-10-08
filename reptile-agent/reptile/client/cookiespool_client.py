import requests

from reptile.client import build_gateway_request_url, check_response
from reptile.util import json_util


def get_random_cookies(platform):
    url = build_gateway_request_url("/cookies-pool/" + platform + "/random")
    resp = requests.get(url)
    check_response(url, None, resp)
    return json_util.json2dict(resp.text)
