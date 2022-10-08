from core.generator import CookiesGenerator, CookiesProvider


class WalmartCookiesGenerator(CookiesGenerator):
    website = 'walmart'

    async def new_cookies(self, username, password):
        """
        生成Cookies
        :param username: 用户名
        :param password: 密码
        :return: 用户名和Cookies
        """
        # 将自己扩展站点Cookies生成函数导入进来，将结果返回
        return await WalmartCookiesProvider(username, password, self.browser).main()


class WalmartCookiesProvider(CookiesProvider):
    login_home_url = "https://login.taobao.com/member/login.jhtml"
    captcha_failed_count = 0

    async def login(self, page):
        # 获取用户名和密码输入框
        username = await page.querySelector('#fm-login-id')
        password = await page.querySelector('#fm-login-password')

        # 输入用户和密码
        await username.type(self.username, options={'delay': 100})
        await password.type(self.password, options={'delay': 100})

    def is_home_page(self, url):
        return "my_taobao.htm?" in url or "https://www.taobao.com/" == url
