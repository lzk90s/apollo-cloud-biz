#!/usr/bin/python3

import logging
import os
import threading
import time

from core.api import app, chrome_runner
from core.config import API_HOST, API_PORT, PYPPETEER_BROWSER_PAGE_MAX_NUM

logging.basicConfig(level=logging.INFO,
                    # filename="app.log",
                    format='%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s')


def run_flask():
    app.run(host=API_HOST, port=API_PORT)


def run_browser():
    while True:
        num = chrome_runner.page_sum()
        if num > PYPPETEER_BROWSER_PAGE_MAX_NUM:
            logging.warning("Page num {} too much, close browser".format(num))
            chrome_runner.stop()
        if not chrome_runner.is_alive():
            logging.warning("Browser not alive, start browser")
            chrome_runner.start(proxy=os.getenv("PROXY"))
        time.sleep(10)


if __name__ == "__main__":
    t = threading.Thread(target=run_browser)
    t.setDaemon(True)
    t.start()
    run_flask()
