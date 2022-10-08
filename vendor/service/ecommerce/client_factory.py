import hashlib

from service.ecommerce.client import ProductClient, OrderClient
from util import class_loader
from . import ALL_CLIENTS


_cache = {}


def _md5(s):
    m = hashlib.md5()
    m.update(s)
    return m.hexdigest()


def _get_client(platform, service_type, client_auth=('', '', '')):
    type = platform + "_" + service_type
    if len(client_auth) < 2:
        raise ValueError("Invalid client auth")
    if type not in ALL_CLIENTS:
        raise ValueError("No client found, " + type)

    unique_key = type + ''.join(filter(lambda s: s, client_auth))
    cache_key = _md5(unique_key.encode("utf-8"))
    if cache_key in _cache:
        return _cache.get(cache_key)

    cls = class_loader.load_class_by_type(ALL_CLIENTS[type])
    ins = cls(client_auth[0], client_auth[1], client_auth[2])
    _cache[cache_key] = ins
    return ins


def get_product_client(platform, client_auth=('', '')):
    mgr = _get_client(platform, "product", client_auth)
    assert isinstance(mgr, ProductClient)
    return mgr


def get_order_client(platform, client_auth=('', '')):
    mgr = _get_client(platform, "order", client_auth)
    assert isinstance(mgr, OrderClient)
    return mgr
