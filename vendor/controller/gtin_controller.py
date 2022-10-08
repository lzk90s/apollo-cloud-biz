from flask import Blueprint, request

from common.restapi import result
from util import gtin_util

gtin = Blueprint('gtin', __name__)


@gtin.route('/batch_allocate', methods=['GET'])
def batch_allocate():
    num = request.args.get("num")
    if not num:
        raise ValueError("Invalid num")
    gtins = [gtin_util.generate_gtin() for i in range(int(num))]
    return result(gtins)
