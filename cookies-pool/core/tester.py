import json
import logging

import requests

from core.db import *


class ValidTester(object):
    website = None
    test_url = None

    def __init__(self):
        self.cookies_db = RedisClient('cookies', self.website)
        self.accounts_db = RedisClient('accounts', self.website)

    def test(self, username, cookies):
        raise NotImplementedError

    def run(self):
        cookies_groups = self.cookies_db.all()
        for username, cookies in cookies_groups.items():
            self.test(username, cookies)


class BaseValidTester(ValidTester):

    def test(self, username, cookies):
        logging.info("正在测试Cookies {}, 用户名{}".format(cookies, username))
        headers = {
            'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
            'accept-encoding': 'gzip, deflate, br',
            'accept-language': 'zh-CN,zh;q=0.9',
            'upgrade-insecure-requests': '1',
            'user-agent': 'Mozilla/5.0 (Windows NT 10.0; '
                          'Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36'
        }
        try:
            cookies = json.loads(cookies)
        except TypeError:
            logging.error("Cookies不合法 {}".format(username))
            self.cookies_db.delete(username)
            logging.warning("删除cookies {}".format(username))
            return
        try:
            # 获取检测Cookies的URL
            response = requests.get(self.test_url, timeout=20, allow_redirects=False, headers=headers,
                                    cookies=self.cookies_to_dict(cookies))
            logging.info("查看获取的响应：{}".format(response))
            if response.status_code == 200:
                logging.info("Cookies有效 - {}".format(username))
            else:
                print(response.status_code, response.headers)
                self.cookies_db.delete(username)
                logging.info("Cookies失效，删除Cookies - {}".format(username))
        except ConnectionError as e:
            logging.error("发生异常{}".format(e.args))

    @classmethod
    def cookies_to_dict(cls, cookies):
        dict = {}
        for cookie in cookies:
            dict[cookie['name']] = cookie['value']
        return dict
