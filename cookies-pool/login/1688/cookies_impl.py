from login.taobao.cookies_impl import TaoBaoCookiesGenerator, TaoBaoCookiesProvider


class A1688CookiesGenerator(TaoBaoCookiesGenerator):
    website = '1688'

    async def new_cookies(self, username, password):
        return await A1688CookiesProvider(username, password, self.browser).main()


class A1688CookiesProvider(TaoBaoCookiesProvider):
    login_home_url = "https://login.taobao.com/?redirect_url=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttps%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttps%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688&style=tao_custom&from=1688web"

    def is_home_page(self, url):
        return str(url).startswith("https://work.1688.com/") or \
               str(url).startswith("https://member.1688.com/") or \
               "https://www.taobao.com/" == url
