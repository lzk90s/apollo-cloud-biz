import asyncio
import logging

from core.generator import CookiesGenerator, CookiesProvider


class GoogleMailCookiesGenerator(CookiesGenerator):
    website = 'googlemail'

    async def new_cookies(self, username, password):
        return await GoogleMailCookiesProvider(username, password, self.browser).main()


class GoogleMailCookiesProvider(CookiesProvider):
    login_home_url = "https://accounts.google.com/signin/v2/identifier?continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&service=mail&sacu=1&rip=1&flowName=GlifWebSignIn&flowEntry=ServiceLogin"

    async def login(self, page):
        # 获取用户名和密码输入框
        username = await page.querySelector('#identifierId')
        if not username:
            logging.error("No user name input")
            return

        # 输入用户名
        await username.type(self.username, options={'delay': 100})

        # 检查是否有下一步的按钮，如果有，就点击按钮
        identifier_next = await page.querySelector('#identifierNext')
        if identifier_next:
            await identifier_next.click()
            await asyncio.sleep(2)

        password = await page.querySelector('#password > div > div > div > input')
        if not password:
            logging.error("No password input")
            return

        # 输入用户和密码
        await password.type(self.password, options={'delay': 100})

        # 提交登录
        password_next = await page.querySelector('#passwordNext')
        if password_next:
            await password_next.click()
            await asyncio.sleep(2)

        logging.info("Login complete")
        await asyncio.sleep(2)

    def is_home_page(self, url):
        return "#inbox" in url
