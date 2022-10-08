from gerapy_pyppeteer import PyppeteerRequest
from reptile.spiders.base import BaseSpider


class AnjukeSpider(BaseSpider):
    name = "anjuke"
    use_cookies = False

    def start_requests(self):
        url_template = "https://www.anjuke.com/hangzhou/cm/{}"
        for page in range(1, 2):
            yield PyppeteerRequest(url=url_template.format(page),
                                   callback=self.parse_community,
                                   wait_for=2,
                                   pre_page_hooks=[self.add_cookies])

    def parse_community(self, response):
        print(response.text)
        pass

    async def add_cookies(self, request, page):
        request.cookies = self.cookies

    async def update_cookies(self, request, page):
        self.cookies = await page.cookies()