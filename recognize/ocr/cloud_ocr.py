import json
import os
import re
import time
from typing import Optional

import requests


def obj2json(obj):
    return json.dumps(obj, default=lambda o: o.__dict__, sort_keys=True, indent=4, ensure_ascii=False)


def json2dict(json_str):
    return json.loads(json_str)


def is_chinese(string: str) -> bool:
    for ch in string:
        if u'\u4e00' <= ch <= u'\u9fff':
            return True
    return False


def get_number_from_str(desc: str):
    pattern = re.compile(r'\d+')
    res = re.findall(pattern, desc)
    res = list(map(int, res))
    res.sort(reverse=True)
    if res:
        return res[0]
    return None


def is_excel_serial_no(txt, max=100) -> bool:
    """
    判断是否是excel的序号，序号暂时最大到100
    :param txt:
    :return:
    """
    num = get_number_from_str(txt)
    if not num:
        return False
    return int(num) < max


def upload_image(token: str, image_bytes: bytes) -> Optional[str]:
    url = "https://cs8.intsig.net/sync/upload_jpg?last=1&pages=&token=" + token
    res = requests.post(url, data=image_bytes)
    if res.status_code != 200:
        print("Upload file failed")
        return None
    data = json2dict(res.text)
    doc_id = data["page_id"]
    return str(doc_id)


def recognize(token: str, doc_id: str) -> str:
    timestamp = int(round(time.time() * 1000))
    url = "https://cs8.intsig.net/sync/cloud_ocr?t=" + str(timestamp) + "&token=" + token
    res = requests.post(url + "&file_name=" + doc_id + ".jpage")
    if res.status_code != 200:
        print("Cloud ocr failed")
        return None

    data = json2dict(res.text)
    ocr_result = json2dict(data["cloud_ocr"])
    text = str(ocr_result['ocr_user_text'])
    content = text.encode('latin').decode('utf-8')
    return content


def parse_recognize_result(content: str) -> Optional[str]:
    ss = content.replace(" ", "").split("\n")
    if not ss:
        return None

    result = []
    for s in ss:
        if is_excel_serial_no(s):
            result.append("\n")
        result.append(s)
        result.append("\t")

    content = "".join(result)
    ss = content.split("\n")
    lines = []
    for s in ss:
        si = s.split("\t")
        if not si:
            continue
        line = si[0]
        for i in range(1, len(si)):
            if is_chinese(si[i]) and is_chinese(si[i - 1]):
                line += si[i]
            else:
                line += "\t" + si[i]
        lines.append(line)

    cc = ("\n".join(lines))
    return cc


def get_token_from_env():
    token = os.getenv("OCR_TOKEN")
    if not token:
        raise Exception("No token")
    return token


def ocr(token: str, image_bytes: bytes) -> Optional[str]:
    if not image_bytes:
        return None

    doc_id = upload_image(token, image_bytes)
    if not doc_id:
        print("Upload image failed")
        return None

    content = recognize(token, doc_id)
    if not content:
        print("Ocr result is empty")
        return None

    return parse_recognize_result(content)
