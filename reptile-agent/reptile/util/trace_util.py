import random

from reptile.client import product_client


def captcha_slice_trace(dis):
    """
    验证码滑动轨迹
    :param dis: 距离
    :return: 轨迹点
    """
    curr = 0
    idx = 0
    trace = product_client.get_captcha_slice_trace()
    while (float(curr) < dis + 50) and (idx < len(trace) - 1):
        idx = idx + 1
        c = trace[idx]
        curr = c[1]
        yield c


def page_scroll_trace(dis, st=2):
    """
    页面滚动轨迹
    :param dis:
    :param st:
    :return:
    """
    trace = []
    t0 = 0.2
    curr = 0
    step = 0
    a = 0.8
    while float(curr) < dis:
        step = step + st
        t = t0 * step
        if curr < dis / 2:
            curr = float(1 / 2 * a * t * t) + random.randrange(0, st * 2)
        elif curr < dis / 8 * 7:
            curr = curr + float(a * t) + random.randrange(0, st * 2)
        else:
            curr = curr + float(1 / 3 * a * t) + random.randrange(0, st * 2)
        trace.append(curr)
    for s in trace:
        yield s
