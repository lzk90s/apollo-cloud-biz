import math
import re


def recognize(desc):
    r = get_max_size_from_str(desc)
    if not r:
        return None
    f = floor_size(r)
    return str(f)


def floor_size(size):
    s = math.floor(int(size) / 10)
    return s * 10


def get_max_size_from_str(desc):
    # 提取字符串中的数字，返回最大的
    pattern = re.compile(r'\d+')
    res = re.findall(pattern, desc)
    res = list(map(int, res))
    res.sort(reverse=True)
    if res:
        return res[0]
    return None


if __name__ == "__main__":
    print(recognize('110/56'))
