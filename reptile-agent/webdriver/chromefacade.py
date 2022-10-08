import os

import pyppeteer
import requests


class ChromeFacade:
    async def new_browser(self, viewport):
        return await self._get_remote_browser(viewport)

    async def _get_remote_browser(self, viewport):
        endpoint = self._get_remote_ws_endpoint()
        browser = await pyppeteer.connect(options={
            "browserWSEndpoint": endpoint,
            'defaultViewport': viewport
        })
        return browser

    def _get_remote_ws_endpoint(self):
        response = requests.get(self.remote_chrome_base_url + "/browser/ws_endpoint")
        if response.status_code != 200:
            raise Exception("Get webdriver remote ws endpoint failed")
        uri = str(response.text)
        # 如果返回的是绝对地址，使用绝对地址，如果返回的是相对地址，拼接url
        url = uri if "://" in uri else self.remote_chrome_base_url + str(response.text)
        return "ws" + url[url.index("://"):]

    @property
    def remote_chrome_base_url(self):
        ev = os.getenv("REMOTE_CHROME_BASE_URL")
        use_docker = os.getenv("USE_DOCKER")
        if ev:
            return ev
        elif use_docker:
            return "http://remote-chrome:45000"
        else:
            return "http://localhost:45000"
