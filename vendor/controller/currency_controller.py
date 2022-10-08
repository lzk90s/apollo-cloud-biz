from flask import request, Blueprint

from common.restapi import result
from service.concurency import concurrency_rate

currency = Blueprint('currency', __name__)


@currency.route('/exchange_rate', methods=['GET'])
def translate():
    from_currency = request.args.get("from")
    to_currency = request.args.get("to")

    if not from_currency or not to_currency:
        raise ValueError("Invalid params")

    if from_currency != "rmb" or to_currency != "dollar":
        raise ValueError("From must is rmb and to must is dollar")

    return result(concurrency_rate.get_concurrency_rate())
