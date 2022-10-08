import requests
from flask import Blueprint, request

from common.restapi import result
from util import gtin_util

map = Blueprint('map', __name__)


@map.route('/poi_search', methods=['GET'])
def poi_search():
    city = request.args.get("city")
    key = request.args.get('key')
    types = request.args.get('types')
    if not city or not key or not types:
        raise ValueError("Invalid param")
    url_pattern = 'https://restapi.amap.com/v3/place/text?key={}&types={}&city={}'
    res = requests.get(url_pattern.format())
    return result(res.text)
