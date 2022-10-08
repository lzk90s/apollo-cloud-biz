import asyncio
import logging

from core.config import *

loop = asyncio.get_event_loop()


class ChromeRunner:
    def __init__(self):
        self.browser = None

    def __del__(self):
        loop.run_until_complete(self.async_close_browser())
        loop.close()

    def start(self, proxy=None):
        loop.run_until_complete(self.async_init_browser(proxy))

    def stop(self):
        loop.run_until_complete(self.async_close_browser())
        self.browser = None

    def restart(self, proxy=None):
        loop.run_until_complete(self.async_restart_browser(proxy))

    def get_ws_endpoint(self):
        if not self.browser:
            raise Exception("Browser not init")
        return self.browser.wsEndpoint

    def is_alive(self):
        if not self.browser:
            return False
        try:
            loop.run_until_complete(self.browser.version())
            return True
        except Exception as e:
            logging.error("Get version failed, {}".format(e.args))
            return False

    def page_sum(self):
        if not self.browser:
            return 0
        return len(self.browser.targets())

    async def async_restart_browser(self, proxy=None):
        await self.async_close_browser()
        await self.async_init_browser(proxy)

    async def async_init_browser(self, proxy=None):
        options = {
            'headless': PYPPETEER_HEADLESS,
            'dumpio': PYPPETEER_DUMPIO,
            'executablePath': PYPPETEER_EXECUTABLE_PATH,
            'devtools': False,
            'defaultViewport': {
                'width': 1360,
                'height': 768,
            },
            'args': [
                '--disable-dev-shm-usage',
                '--no-first-run',
                '--disable-extensions',
                '--hide-scrollbars',
                '--mute-audio',
                '--no-sandbox',
                '--disable-setuid-sandbox',
                '--disable-gpu',
                '--no-zygote',
                '--remote-debugging-port=' + str(PYPPETEER_REMOTE_DEBUGGING_PORT),
                '--disable-blink-features=AutomationControlled', ],
            'handleSIGINT': False,
            'handleSIGTERM': False,
            'handleSIGHUP': False,
            'autoClose': True,
            'ignoreDefaultArgs': ['--enable-automation']
        }
        if PYPPETEER_SINGLE_PROCESS:
            options['args'].append('--single-process')
        if proxy:
            options['args'].append('--proxy-server=' + str(proxy))

        logging.info("Open browser, options = {}".format(options))
        launcher = pyppeteer.launcher.Launcher(options)
        port = PYPPETEER_REMOTE_DEBUGGING_PORT
        launcher.port = port
        launcher.url = f'http://0.0.0.0:{str(port)}'
        self.browser = await launcher.launch()
        logging.info("Open browser succeed.")

    async def async_close_browser(self):
        if self.browser:
            logging.info("Close browser")
            await self.browser.close()
            del self.browser
