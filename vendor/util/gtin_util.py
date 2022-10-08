import math
import random


def get_random_int(min, max):
    return random.randint(min, max)


def cks(code):
    total = 0
    value = str(code)
    for i in range(len(value)):
        c = value[i]
        if i % 2 == 1:
            total = total + int(c)
        else:
            total = total + 3 * int(c)
    return (10 - (total % 10)) % 10


def generate_gtin():
    pow = 12
    value = get_random_int(math.pow(10, pow), math.pow(10, pow + 1))
    chk = cks(value)
    return str(value) + str(chk)


if __name__ == "__main__":
    for i in range(10):
        print(generate_gtin())
