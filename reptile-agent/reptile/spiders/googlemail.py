from gerapy_pyppeteer import PyppeteerRequest
from reptile.spiders.base import BaseSpider


class GoogleMailSpider(BaseSpider):
    name = "googlemail"
    use_cookies = True

    def start_requests(self):
        url = "https://mail.google.com/mail/u/0/#inbox"

        yield PyppeteerRequest(url=url,
                               callback=self.parse_mail,
                               wait_for=2,
                               pre_page_hooks=[self.add_cookies])

    def parse_mail(self, response):
        pass

    async def add_cookies(self, request, page):
        request.cookies = self.cookies

    async def update_cookies(self, request, page):
        self.cookies = await page.cookies()
