class DictEx(dict):
    def __init__(self, **entries):
        self.__dict__.update(entries)

    __setattr__ = dict.__setitem__
    __getattr__ = dict.__getitem__


def dict2obj(dict_obj):
    if not isinstance(dict_obj, dict):
        return dict_obj
    d = DictEx()
    for k, v in dict_obj.items():
        if isinstance(v, list):
            d[k] = []
            for o in v:
                d[k].append(dict2obj(o))
        else:
            d[k] = dict2obj(v)
    return d
