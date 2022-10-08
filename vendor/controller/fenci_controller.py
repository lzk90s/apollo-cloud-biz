from flask import Blueprint, request

from common.restapi import result
from service.fenci import jieba_fenci

fenci = Blueprint('fenci', __name__)


@fenci.route('/cut', methods=['GET'])
def cut():
    content = request.args.get("content")
    if not content:
        raise ValueError("Invalid content")
    return result(jieba_fenci.cut_words(content))
