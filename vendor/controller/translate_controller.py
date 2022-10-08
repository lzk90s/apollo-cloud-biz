import logging

from flask import request, Blueprint

from common.restapi import result
from service.translate import alibaba

translator = Blueprint('translator', __name__)

ts = alibaba.AlibabaTranslator()


@translator.route('/translate', methods=['POST'])
def translate():
    from_lang = request.args.get("from_lang")
    to_lang = request.args.get("to_lang")
    json_body = request.json
    text = str(json_body["text"])
    if not from_lang or not to_lang or not text:
        raise ValueError("Invalid params")

    logging.info("Translate text {}".format(text))

    result_list = []
    text_list = text.split("\n")
    for t in text_list:
        r = ts.translate(from_lang, to_lang, t)
        result_list.append(r)

    return result("\n".join(result_list))
