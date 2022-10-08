import json
import random
import re
import time

import bs4
import requests
from bs4 import BeautifulSoup

from tool import config


def obj2json(obj, indent=4):
    return json.dumps(obj, default=lambda o: o.__dict__, sort_keys=True, indent=indent, ensure_ascii=False)


def json2dict(json_str) -> dict:
    return json.loads(json_str)


def check_response(res):
    if not res:
        raise ValueError()
    if res.status_code != 200:
        raise ValueError()


def is_community_exist(id):
    url = 'http://localhost:22334/community/page'
    res = requests.get(url, params={'code': id})
    check_response(res)
    result = json2dict(res.text)
    return 'data' in result and result['data']['totalCount'] == 1


def save_community(comm_info):
    url = 'http://localhost:22334/community'
    res = requests.post(url, json=comm_info)
    check_response(res)


def get_number_from_str(desc: str):
    pattern = re.compile(r'\d+')
    res = re.findall(pattern, desc)
    res = list(map(int, res))
    res.sort(reverse=True)
    if res:
        return res[0]
    return None


def parse_community_id_from_url(url):
    return get_number_from_str(url)


def parse_area_from_address(address):
    return address[0: str(address).index('-')]


def parse_community(url):
    headers = {
        'User-Agent': config.USER_AGENT
    }

    cookie = {
        'Cookie': config.COOKIES
    }

    print("parse community, url {}".format(url))

    res = requests.get(url, cookies=cookie, headers=headers)
    soup = bs4.BeautifulSoup(res.text, 'lxml')

    id = get_number_from_str(url)

    title_tag = soup.find('h1', attrs={'class': 'title'})
    if not title_tag:
        print("no title, url {}".format(url))
        return None
    title = title_tag.text

    sub_title_tag = soup.find('p', attrs={'class': 'sub-title'})
    if not sub_title_tag:
        print("no title, url {}".format(url))
        return None
    sub_title = sub_title_tag.text

    price_tag = soup.find('div', attrs={'class': 'house-price'})
    price_span_tag = None
    if price_tag:
        price_span_tag = price_tag.find('span', attrs={'class': 'average'})
    if not price_span_tag:
        print("no price, url {}".format(url))
        return None
    price = int(price_span_tag.text)

    main_info = soup.find('div', attrs={'class': 'maininfo'})
    if not main_info:
        print("no main_info, url {}".format(url))
        return None

    info_list_tag = main_info.find('div', attrs={'class': 'info-list'})
    if not info_list_tag:
        print("no info_list, url {}".format(url))
        return None

    label_value = {}
    item_tag_list = info_list_tag.find_all('div', attrs={'class': 'column-2'})
    for item_tag in item_tag_list:
        label_tag = item_tag.find('div', attrs={'class': 'label'})
        label = label_tag.text
        value_tag = item_tag.find('div', attrs={'class': 'value'})
        value = value_tag.text.strip()
        if label == '竣工时间':
            value = get_number_from_str(value)
        elif label == '总户数':
            value = get_number_from_str(value)
        label_value[label] = value

    label_value['名称'] = title
    label_value['地址'] = sub_title
    label_value['价格'] = price
    label_value['编码'] = id
    label_value['区域'] = parse_area_from_address(sub_title)

    kv_map = {
        '编码': 'code',
        '名称': 'name',
        '地址': 'address',
        '区域': 'area',
        '竣工时间': 'deliveryYear',
        '权属类别': 'ownershipType',
        '价格': 'price',
        '总户数': 'totalHousehold'
    }

    community_info = {}
    for k, v in label_value.items():
        if k not in kv_map:
            continue
        community_info[kv_map.get(k)] = v

    if community_info['ownershipType'] != '商品房住宅':
        print("ignore ownership {}".format(community_info))
        return None

    return community_info


def parse_xml(content):
    soup = BeautifulSoup(content, 'html.parser')
    body = soup.find('ul', attrs={'class': 'P3'})
    if not body:
        return None

    links = body.find_all('a')

    result = []
    for link in links:
        url = link.attrs['href']
        name = link.text
        code = get_number_from_str(url)
        if not code:
            print("invalid code, url {}".format(url))
            continue
        url = 'https://hangzhou.anjuke.com/community/view/{}'.format(code)
        result.append({'url': url, 'name': name, 'code': code})
    return result


def test():
    f = "html.xml"
    with open(f, 'r') as fin:
        c = fin.read()
        community_list = parse_xml(c)
        if not community_list:
            return
        for community in community_list:
            url = community['url']
            id = parse_community_id_from_url(url)
            if is_community_exist(id):
                print('community already exist, url {}'.format(url))
                continue

            info = parse_community(url)
            if info:
                print(obj2json(info))
                save_community(info)
            time.sleep(random.randint(2, 5))


if __name__ == "__main__":
    test()
