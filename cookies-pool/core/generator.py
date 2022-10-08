import asyncio
import json
import logging

import requests

from core.db import RedisClient
from webdriver.chromefacade import ChromeFacade
from webdriver.pretend import PRETEND_SCRIPTS

loop = asyncio.new_event_loop()
asyncio.set_event_loop(loop)


def get_remote_chrome():
    requests.get()


class CookiesGenerator(object):
    website = None

    def __init__(self):
        """
        父类, 初始化一些对象
        :param website: 名称
        :param webdriver: 浏览器, 若不使用浏览器则可设置为 None
        """
        self.cookies_db = RedisClient('cookies', self.website)
        self.accounts_db = RedisClient('accounts', self.website)
        self.chrome_facade = ChromeFacade()
        self.browser = None

    async def init_browser(self):
        self.browser = await self.chrome_facade.new_browser()

    async def new_cookies(self, username, password):
        """
        新生成Cookies，子类需要重写
        :param username: 用户名
        :param password: 密码
        :return:
        """
        raise NotImplementedError

    def process_cookies(self, cookies):
        """
        处理Cookies
        :param cookies:
        :return:
        """
        return cookies

    def run(self):
        """
        运行, 得到所有账户, 然后顺次模拟登录
        :return:
        """
        asyncio.get_event_loop().run_until_complete(asyncio.ensure_future(self._run()))

    def close(self):
        pass

    async def _run(self):
        accounts_usernames = self.accounts_db.usernames()
        cookies_usernames = self.cookies_db.usernames()

        logging.info("Account users : {}".format(accounts_usernames))
        logging.info("Cookies users : {}".format(cookies_usernames))

        browser_initialized = False
        for username in accounts_usernames:
            if username not in cookies_usernames:
                # 检查浏览器是否有初始化，若没有初始化，就初始化浏览器
                if not browser_initialized:
                    await self.init_browser()
                    browser_initialized = True

                password = self.accounts_db.get(username)
                logging.info('正在生成Cookies, 账号{}'.format(username))
                result = await self.new_cookies(username, password)
                status = result.get('status')
                content = result.get('content')
                # 成功获取
                if status == 1:
                    cookies = self.process_cookies(content)
                    logging.info('成功获取到Cookies{}'.format(cookies))
                    if self.cookies_db.set(username, json.dumps(cookies)):
                        logging.info('成功保存Cookies')
                # 密码错误，移除账号
                elif status == 2:
                    print(content)
                    if self.accounts_db.delete(username):
                        logging.info('成功删除账号')
                else:
                    logging.info(content)
        else:
            logging.info('所有账号都已经成功获取Cookies')

    async def _close(self):
        """
        关闭
        :return:
        """
        # if self.webdriver:
        #     logging.info('Closing Browser')
        #     del self.webdriver
        pass


class CookiesProvider:
    login_home_url = None
    page = None

    def __init__(self, username, password, browser):
        self.username = username
        self.password = password
        self.browser = browser

    def __del__(self):
        asyncio.ensure_future(self.close())

    @classmethod
    async def load_js(cls, page):
        for script in PRETEND_SCRIPTS:
            await page.evaluateOnNewDocument(script)

    async def close(self):
        if not self.page:
            return
        logging.info("Close page")
        await self.page.close()
        del self.page
        self.page = None
        logging.info("Close page succeed")

    async def open(self):
        """
        打开网页输入用户名，密码，并点击
        :return: None
        """
        page = await self.browser.newPage()
        self.page = page

        await self.load_js(page)

        await page.goto(
            self.login_home_url,
            options={
                'timeout': 1000 * 30,
                'waitUntil': 'domcontentloaded'
            }
        )

        await self.login(page)

    async def password_error(self):
        """
        判断是否密码错误
        :return:
        """
        try:
            return await self.page.querySelector('.error') is not None
        except Exception as e:
            logging.error("Error {}".format(e.args))
            return False

    async def login_successfully(self):
        """
        判断是否登陆成功
        :return:
        """
        cnt = 8
        while cnt > 0:
            logging.info("Current url is {}".format(self.page.url))
            if self.is_home_page(self.page.url):
                logging.info("Login succeed")
                return True
            await asyncio.sleep(10)
            cnt = cnt - 1
        logging.info("Login failed")
        return False

    async def get_cookies(self):
        """
        登陆成功获取Cookies
        :return:
        """
        return await self.page.cookies()

    async def main(self):
        """
        登陆获取Cookies如入口
        :return:
        """
        try:
            await self.open()
            await asyncio.sleep(10)
            r = await self.get_result()
            return r
        except Exception as e:
            logging.error("Get cookies failed, {}".format(e.args))
        finally:
            await self.close()

    async def get_result(self):
        # 如果用户名或者密码错误
        if await self.password_error():
            return {
                'status': 2,
                'content': '你输入的账号和密码不匹配'
            }
        # 如果登陆成功
        if await self.login_successfully():
            # 获取登陆后的Cookies
            cookies = await self.get_cookies()
            logging.info("Get cookies succeed, cookies is {}".format(cookies))
            return {
                'status': 1,
                'content': cookies
            }
        else:
            return {
                'status': 3,
                'content': '登陆失败'
            }

    def is_home_page(self, url):
        return True

    async def login(self, page):
        pass
