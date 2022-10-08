import logging
import traceback

from flask import Blueprint, request, Response

from common.exception import BizException
from util.json_util import obj2json

exception_advice = Blueprint('exception', __name__)


@exception_advice.app_errorhandler(BizException)
def handle_biz_exception(error):
    traceback.print_exc()
    args = request.args
    logging.error("Business exception: {}, args {}".format(error, args))
    assert isinstance(error, BizException)
    return Response(content_type="application/json", status=500,
                    response=obj2json(BizException(";".join(error.args), 500)))


@exception_advice.app_errorhandler(ValueError)
def handle_value_exception(error):
    traceback.print_exc()
    args = request.args
    logging.error("Value exception: {}, args {}".format(error, args))
    assert isinstance(error, ValueError)
    return Response(content_type="application/json", status=500,
                    response=obj2json(BizException(";".join(error.args), 500)))


@exception_advice.app_errorhandler(Exception)
def handle_default_exception(error):
    traceback.print_exc()
    args = request.args
    assert isinstance(error, Exception)
    logging.error("Exception: {}, args {}".format(error, args))
    return Response(content_type="application/json", status=500,
                    response=obj2json(BizException(";".join(error.args), 500)))
