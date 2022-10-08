# -*- coding: utf-8 -*-

BOT_NAME = 'reptile'

LOG_LEVEL = "INFO"

SPIDER_MODULES = ['reptile.spiders']
NEWSPIDER_MODULE = 'reptile.spiders'

# Obey robots.txt rules
ROBOTSTXT_OBEY = False

# Configure maximum concurrent requests performed by Scrapy (default: 16)
CONCURRENT_REQUESTS = 1

# Configure a delay for requests for the same website (default: 0)
# See https://docs.scrapy.org/en/latest/topics/settings.html#download-delay
# See also autothrottle settings and docs
#DOWNLOAD_DELAY = 4  # 随机等待时间
#RANDOMIZE_DOWNLOAD_DELAY = True
# The download delay setting will honor only one of:
CONCURRENT_REQUESTS_PER_DOMAIN = 1
CONCURRENT_REQUESTS_PER_IP = 1

# Disable cookies (enabled by default)
# COOKIES_ENABLED = False

# Disable Telnet Console (enabled by default)
# TELNETCONSOLE_ENABLED = False

# Override the default request headers:
DEFAULT_REQUEST_HEADERS = {
    "accept-encoding": "gzip, deflate, br",
    "accept-language": "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7",
    "cache-control": "max-age=0",
    "sec-fetch-mode": "navigate",
    "sec-fetch-site": "none",
    "sec-fetch-user": "?1",
    "upgrade-insecure-requests": "1",
    "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"
}

# Configure item pipelines
# See https://docs.scrapy.org/en/latest/topics/item-pipeline.html
ITEM_PIPELINES = {
    'reptile.pipelines.ReptilePipeline': 1,
}

# 添加Splash中间件
DOWNLOADER_MIDDLEWARES = {
    'scrapy.downloadermiddlewares.cookies.CookiesMiddleware': 700,
    # Enable Splash to render javascript
    'gerapy_pyppeteer.downloadermiddlewares.PyppeteerMiddleware': 543,
    'scrapy.downloadermiddlewares.httpcompression.HttpCompressionMiddleware': 810,
}

HTTPERROR_ALLOWED_CODES = [302, 304]

GERAPY_ENABLE_REQUEST_INTERCEPTION = False

GERAPY_PYPPETEER_SLEEP = 0

GERAPY_PYPPETEER_DOWNLOAD_TIMEOUT = 30

RETRY_ENABLED = False

GERAPY_PYPPETEER_IS_MOBILE = False
GERAPY_PYPPETEER_HAS_TOUCH = False
GERAPY_PYPPETEER_WINDOW_WIDTH = 1400
GERAPY_PYPPETEER_WINDOW_HEIGHT = 800
GERAPY_PYPPETEER_DEFAULT_USER_AGENT = "5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36"
