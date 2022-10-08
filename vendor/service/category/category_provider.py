import json
import os

import requests

from util import json_util

_CONFIG_BASE_URL = "http://registry:8848"

_GATEWAY_BASE_URL = os.getenv("GATEWAY_BASE_URL")
if _GATEWAY_BASE_URL:
    _CONFIG_BASE_URL = _GATEWAY_BASE_URL + "/config"

_CATEGORY_LIST = None
_QUERY_COUNT = 0


class Category:
    def __init__(self, name, id, url, description, vendor=""):
        self.name = name
        self.id = id
        self.url = url
        self.description = description
        self.vendor = vendor

    def __str__(self):
        return json.dump(self)


def get_config(data_id, group="DEFAULT_GROUP"):
    url = _CONFIG_BASE_URL + "/nacos/v1/cs/configs?dataId=" + data_id + "&group=" + group
    resp = requests.get(url)
    if resp.status_code != 200:
        raise Exception("Failed to get config")
    return json_util.json2dict(resp.text)


def get_category_by_name(platform, name):
    all_category = get_all_category(platform)
    for i in all_category:
        if str(i.name).strip() == str(name).strip():
            return i
    return None


def get_category_by_name(platform, name):
    all_category = get_all_category(platform)
    for i in all_category:
        if str(i.name).strip() == str(name).strip():
            return i
    return None


def get_all_category(platform):
    global _CATEGORY_LIST, _QUERY_COUNT
    if _QUERY_COUNT > 20:
        _CATEGORY_LIST = None
        _QUERY_COUNT = 0
    if not _CATEGORY_LIST:
        category_list = get_config(platform + "_categories")
        _CATEGORY_LIST = [Category(c["name"], c["id"], c["url"], c["description"]) for c in category_list]
    _QUERY_COUNT = _QUERY_COUNT + 1
    return _CATEGORY_LIST


if __name__ == "__main__":
    print(json_util.obj2json(get_all_category()))
