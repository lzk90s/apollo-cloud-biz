import logging
import os

import jieba_fast as jieba

from util import json_util

user_dict_file = "service/fenci/user_dict.txt"
if os.path.exists(user_dict_file):
    logging.info("Load user dict from {}".format(user_dict_file))
    jieba.load_userdict(user_dict_file)

FILTER_OUT_KEYWORDS = [
    '一件代发',
    '批发',
    '一件代销',
    '2020',
    '2021',
    '2019',
    '跨境',
    '外贸',
    '包邮'
]


def cut_words(cont):
    result = jieba.lcut(cont)
    result = list(filter(lambda s: s and s.strip() and s not in FILTER_OUT_KEYWORDS, result))
    logging.info("Cut words, {} ==> {}".format(cont, json_util.obj2json(result, indent=None)))
    return result
#
#
# s = '韩版童装秋季新品批发 2021儿童毛衣 男宝宝衣服针织衫一件代销'
# for x, w in jieba.analyse.textrank(s, withWeight=True):
#     print('%s %s' % (x, w))
