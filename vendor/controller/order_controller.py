import time

from flask import request, Blueprint

from common.restapi import get_auth_from_req, result
from service.ecommerce.client_factory import get_order_client

order = Blueprint('order', __name__)


@order.route('/list_unhandled_order/<platform>', methods=['GET'])
def list_unhandled_order(platform):
    return result(get_order_client(platform, get_auth_from_req()).list_unhandled_order())


@order.route('/list_refund_order/<platform>', methods=['GET'])
def list_refund_order(platform):
    start_time = request.args.get('startTime')
    end_time = request.args.get('endTime')
    if not start_time:
        start_time = time.strftime("%Y-%m-%d", time.localtime())
    return result(get_order_client(platform, get_auth_from_req()).list_refund_order(start_time, end_time))
