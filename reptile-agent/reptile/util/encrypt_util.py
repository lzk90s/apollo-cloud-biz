import hashlib


def md5(s):
    m = hashlib.md5()
    m.update(str(s).encode("utf-8"))
    return m.hexdigest()
