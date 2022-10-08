import re
import urllib

import pandas as pd
import requests
import baostock as bs


def get_all_stock():
    bs.login()
    rs = bs.query_stock_industry()
    industry_list = []
    while (rs.error_code == '0') & rs.next():
        industry_list.append(rs.get_row_data())
    return pd.DataFrame(industry_list, columns=rs.fields)


def get_daily_k(code, start='19900101', end=''):
    url_mod = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s"
    url = url_mod % (code, start, end)
    df = pd.read_csv(url, encoding='gb2312')
    return df


def get_stock_hq(code):
    url_mod = "https://api.money.126.net/data/feed/{},money.api%5D"
    url = url_mod.format(code)
    res = requests.get(url)
    if res.status_code != 200:
        raise Exception()
    json = str(res.text).strip()[len('_ntes_quote_callback('):-2]
    if not json:
        print("hq date not found for code {}".format(code))
        return None
    return pd.read_json(json)


if __name__ == "__main__":
    d = get_all_stock()
    print(d)
