import json

from reptile.util import dict_util
from reptile.util.dict_util import DictEx


def obj2json(obj, indent=4) -> str:
    return json.dumps(obj, default=lambda o: o.__dict__, sort_keys=True, indent=indent, ensure_ascii=False)


def json2dict(json_str) -> dict:
    return json.loads(json_str)


def json2obj(json_str) -> DictEx:
    return dict_util.dict2obj(json.loads(json_str))
