import base64
import logging

from flask import request, Blueprint, Response

from core import color_recognize, size_recognize, age_recognize, ocr_recognize
from ocr import cloud_ocr
from util import json_util

recognition = Blueprint('recognition', __name__)


@recognition.route('/color_category', methods=['POST'])
def recognize_color():
    desc_list = request.json
    assert isinstance(desc_list, list)
    result_list = []
    log_msg_map = {}
    for desc in desc_list:
        r = color_recognize.recognize(desc)
        result_list.append(r)
        log_msg_map[desc] = r
    logging.info("Recognize color category, {}".format(json_util.obj2json(log_msg_map)))
    return Response(json_util.obj2json(result_list))


@recognition.route('/clothing_size_category', methods=['POST'])
def recognize_clothing_size():
    desc_list = request.json
    assert isinstance(desc_list, list)
    result_list = []
    log_msg_map = {}
    for desc in desc_list:
        r = size_recognize.recognize(desc)
        result_list.append(r)
        log_msg_map[desc] = r
    logging.info("Recognize size category, {}".format(json_util.obj2json(log_msg_map)))
    return Response(json_util.obj2json(result_list))


@recognition.route('/age_category', methods=['POST'])
def recognize_age_category():
    desc_list = request.json
    assert isinstance(desc_list, list)
    result_list = []
    log_msg_map = {}
    for desc in desc_list:
        r = age_recognize.recognize(desc)
        result_list.append(r)
        log_msg_map[desc] = r
    logging.info("Recognize age category, {}".format(json_util.obj2json(log_msg_map)))
    return Response(json_util.obj2json(result_list))


@recognition.route('/image_ocr', methods=['POST'])
def recognize_image_ocr():
    url_list = request.json
    assert isinstance(url_list, list)
    result_list = []
    log_msg_map = {}
    for url in url_list:
        r = ocr_recognize.recognize(url)
        result_list.append(r)
        log_msg_map[url] = r
    logging.info("Recognize image ocr, {}".format(json_util.obj2json(log_msg_map)))
    return Response(json_util.obj2json(result_list))


@recognition.route('/excel_image_ocr_base64', methods=['POST'])
def recognize_excel_image_ocr():
    base64_image = request.data
    if not base64_image:
        raise ValueError()
    image_bytes = base64.decodebytes(base64_image)
    content = cloud_ocr.ocr(cloud_ocr.get_token_from_env(), image_bytes)
    logging.info("Recognize excel image ocr, {}".format(content))
    return Response(content, content_type="text/plain")
