# -*- coding: utf-8 -*-

import hashlib
import os
import pickle

cache_root_dir = 'cache'

if not os.path.exists(cache_root_dir):
    os.makedirs(cache_root_dir)


def md5(s):
    m = hashlib.md5()
    m.update(s)
    return m.hexdigest()


def cache_key(f, *args, **kwargs):
    args = [arg for arg in args if isinstance(arg, str)]
    s = '%s-%s-%s' % (f.__name__, str(args), str(kwargs))
    s = s.encode("utf-8")
    return os.path.join(cache_root_dir, '%s.dump' % md5(s))


def cache(f):
    def wrap(*args, **kwargs):
        fn = cache_key(f, *args, **kwargs)
        if os.path.exists(fn):
            with open(fn, 'rb') as fr:
                return pickle.load(fr)

        obj = f(*args, **kwargs)
        with open(fn, 'wb') as fw:
            pickle.dump(obj, fw)
        return obj

    return wrap


@cache
def add(a, b):
    return a + b


if __name__ == '__main__':
    print(add(3, 4))
    print(add(3, 4))
    print(add(8, 4))
    print(add(4, 8))
