import random


def slice_move_trace(dis, st=2):
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
