import logging

from flask import Blueprint, jsonify, request, Response

from common import exception

exception_advice = Blueprint('exception', __name__)


@exception_advice.app_errorhandler(exception.BizException)
def handle_biz_exception(error):
    args = request.args
    logging.error("Business exception: {}, args {}".format(error, args))
    assert isinstance(error, exception.BizException)
    response = jsonify(error.to_dict())
    response.status_code = 500
    return response


@exception_advice.app_errorhandler(ValueError)
def handle_value_exception(error):
    args = request.args
    logging.error("Value exception: {}, args {}".format(error, args))
    assert isinstance(error, ValueError)
    response = Response()
    response.status_code = 500
    response.data = error
    return response


@exception_advice.app_errorhandler(Exception)
def handle_default_exception(error):
    args = request.args
    logging.error("Exception: {}, args {}".format(error, args))
    response = Response()
    response.status_code = 500
    response.data = error
    return response
