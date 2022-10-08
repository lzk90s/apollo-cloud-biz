import json

from util import dict_util


def obj2json(obj, indent=4):
    return json.dumps(obj, default=lambda o: o.__dict__, sort_keys=True, indent=indent, ensure_ascii=False)


def json2dict(json_str):
    return json.loads(json_str)


def json2obj_ext(json_str, obj_type):
    return json.loads(json_str, cls=obj_type)


def json2obj(json_str):
    return dict_util.dict2obj(json.loads(json_str))
