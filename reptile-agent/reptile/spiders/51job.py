from scrapy import Request

from gerapy_pyppeteer import PyppeteerRequest
from reptile.spiders.base import BaseSpider


class GoogleMailSpider(BaseSpider):
    name = "51job"
    use_cookies = True

    def start_requests(self):
        url = "https://search.51job.com/list/080200,000000,0000,00,9,99,+,2,1.html"

        yield PyppeteerRequest(url=url,
                               callback=self.parse,
                               wait_for=2,
                               pre_page_hooks=[self.add_cookies])

    def parse(self, response, **kwargs):
        print(response.text)
        pass

    async def add_cookies(self, request, page):
        request.cookies = self.cookies

    async def update_cookies(self, request, page):
        self.cookies = await page.cookies()
