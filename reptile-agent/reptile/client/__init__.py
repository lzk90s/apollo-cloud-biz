import os

_GATEWAY_BASE_URL = os.getenv("GATEWAY_BASE_URL")
if not _GATEWAY_BASE_URL:
    _GATEWAY_BASE_URL = "http://gateway:28888/api"


def build_gateway_request_url(uri):
    return _GATEWAY_BASE_URL + uri


def check_response(url, params, resp):
    if resp.status_code != 200:
        raise Exception('Invoke http {} failed, params {}, result {}'.format(url, params, resp.status_code))
