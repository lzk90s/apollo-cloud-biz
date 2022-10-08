import asyncio
import logging
import math
import random
import time

from core.generator import CookiesGenerator, CookiesProvider
from util import trace_util


class TaoBaoCookiesGenerator(CookiesGenerator):
    website = 'taobao'

    async def new_cookies(self, username, password):
        return await TaoBaoCookiesProvider(username, password, self.browser).main()


class TaoBaoCookiesProvider(CookiesProvider):
    login_home_url = "https://login.taobao.com/member/login.jhtml"
    captcha_failed_count = 0

    async def login(self, page):
        # 获取用户名和密码输入框
        username = await page.querySelector('#fm-login-id')
        password = await page.querySelector('#fm-login-password')

        # 输入用户和密码
        await username.type(self.username, options={'delay': 100})
        await password.type(self.password, options={'delay': 100})

        # 先检查一下是否存在验证码，如果不存在，点击登录按钮
        flag = await self.check_captcha(page)
        if flag is None:
            password_login = await page.querySelector('.password-login')
            if password_login:
                await password_login.click()
                await asyncio.sleep(1)
        # 再检查一次验证码
        await self.check_captcha(page)

    async def check_captcha(self, page):
        flag = None
        for i in range(3):
            flag = await self._check_captcha(page)
        return flag

    async def _check_captcha(self, page):
        original_page = page
        iframe = await page.querySelector('iframe')
        if iframe:
            page = await iframe.contentFrame()

        captcha_id = "#nc_1_n1t"
        # 没有验证码直接返回
        slider = await page.querySelector(captcha_id)
        nocaptcha = await page.querySelector("#nocaptcha")
        if not slider and not nocaptcha:
            logging.info("No captcha")
            return None

        # 获取验证码div
        slider = await page.querySelector(captcha_id)
        if not slider:
            logging.info("No captcha")
            return None

        rect = await slider.boundingBox()
        if not rect:
            logging.info("Get bounding box failed")
            return None

        # await page.hover(captcha_id)
        # 滑动滑块
        x = rect["x"]
        y = rect["y"]
        w = rect["width"]
        h = rect["height"]
        x = x + 10
        y = y + math.floor(h / 2)
        mouse = original_page.mouse
        await mouse.move(x, y)
        await mouse.down()
        logging.info("Slice start")
        traces = trace_util.slice_move_trace(w)
        for s in traces:
            _y = random.randrange(-10, 10)
            await mouse.move(x + s, y + _y)
            time.sleep(0.02)
        await mouse.up()
        logging.info("Slice stop")

        await asyncio.sleep(2)

        logging.info("Captcha check succeed")

        return True

    def is_home_page(self, url):
        return "my_taobao.htm?" in url or "https://www.taobao.com/" == url


if __name__ == "__main__":
    result = TaoBaoCookiesProvider('your account', 'your password').main()
    print(result)
