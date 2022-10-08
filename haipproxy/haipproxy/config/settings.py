"""
Settings for global.
"""
#####################################################################
# Scrapy settings of this project
#####################################################################
# scrapy basic info
import os

BOT_NAME = 'haiproxy'
SPIDER_MODULES = ['haipproxy.crawler.spiders', 'haipproxy.crawler.validators']
NEWSPIDER_MODULE = 'haipproxy.crawler'

# downloader settings
ROBOTSTXT_OBEY = False
COOKIES_ENABLED = False
DOWNLOAD_TIMEOUT = 30

# to aviod infinite recursion
DEPTH_LIMIT = 100
CONCURRENT_REQUESTS = 30

# don't filter anything, also can set dont_filter=True in Request objects
DUPEFILTER_CLASS = 'scrapy.dupefilters.BaseDupeFilter'
HTTPCACHE_ENABLED = False
GFW_PROXY = 'http://127.0.0.1:8123'

# splash settings.If you use docker-compose,SPLASH_URL = 'http://splash:8050'
SPLASH_URL = 'http://127.0.0.1:8050'
if os.getenv("ISDOCKER"):
    SPLASH_URL = 'http://splash:8050'

# extension settings
RETRY_ENABLED = False
TELNETCONSOLE_ENABLED = False

UserAgentMiddleware = 'haipproxy.crawler.middlewares.UserAgentMiddleware'
ProxyMiddleware = 'haipproxy.crawler.middlewares.ProxyMiddleware'
DOWNLOADER_MIDDLEWARES = {
    UserAgentMiddleware: 543,
    ProxyMiddleware: 543,
    'scrapy_splash.SplashCookiesMiddleware': 723,
    # it should be prior to HttpProxyMiddleware
    'scrapy_splash.SplashMiddleware': 725,
    'scrapy.downloadermiddlewares.httpcompression.HttpCompressionMiddleware': 810,
}

SPIDER_MIDDLEWARES = {
    'scrapy_splash.SplashDeduplicateArgsMiddleware': 100,
}

# scrapy log settings
LOG_LEVEL = 'DEBUG'
# LOG_FILE = 'logs/haipproxy.log'


#####################################################################
# Custom settings of this project
#####################################################################

# redis settings.If you use docker-compose, REDIS_HOST = 'redis'
# if some value is empty, set like this: key = ''
REDIS_HOST = '127.0.0.1'
if os.getenv("ISDOCKER"):
    REDIS_HOST = 'redis'
REDIS_PORT = 6379
REDIS_PASSWORD = ''
REDIS_DB = 1

# proxies crawler's settings
SPIDER_FEED_SIZE = 10

# custom validator settings
VALIDATOR_FEED_SIZE = 100

# time to live of proxy ip resources
TTL_VALIDATED_RESOURCE = 10  # minutes

# check ports
VALIDATOR_CHECK_PORTS = None
# VALIDATOR_CHECK_PORTS = {
#     80: 500,
#     8080: 100,
#     3128: 100,
#     8081: 100,
#     9080: 100,
#     1080: 100,
#     21: 300,
#     23: 200,
#     53: 300,
#     1863: 200,
#     2289: 100,
#     443: 500,
#     69: 100,
#     22: 500,
#     25: 200,
#     110: 200,
#     7001: 100,
#     9090: 100,
#     3389: 500,
#     1521: 500,
#     1158: 300,
#     2100: 100,
#     1433: 200,
#     3306: 500,
#     5631: 100,
#     5632: 100,
#     5000: 200,
#     8888: 200
# }

# squid settings on linux os
# execute sudo chown -R $USER /etc/squid/ and
# sudo chown -R $USER /var/log/squid/cache.log at first
SQUID_BIN_PATH = '/usr/sbin/squid'  # mac os '/usr/local/sbin/squid'
SQUID_CONF_PATH = '/etc/squid/squid.conf'  # mac os '/usr/local/etc/squid.conf'
SQUID_TEMPLATE_PATH = '/etc/squid/squid.conf.backup'  # mac os /usr/local/etc/squid.conf.backup

# client settings

# client picks proxies which's response time is between 0 and LONGEST_RESPONSE_TIME seconds
LONGEST_RESPONSE_TIME = 15

# client picks proxies which's score is not less than LOWEST_SCORE
LOWEST_SCORE = 7

# weight
LOWEST_WEIGHT = 5000

# if the total num of proxies fetched is less than LOWES_TOTAL_PROXIES, haipproxy will fetch more
# more proxies with lower quality
LOWEST_TOTAL_PROXIES = 5
# if no origin ip is given, request will be sent to https://httpbin.org/ip
ORIGIN_IP = ''

API_LISTEN_HOST = '0.0.0.0'
API_LISTEN_PORT = 55000

#####################################################################
# monitor and bug trace
#####################################################################
USE_SENTRY = True
SENTRY_DSN = 'http://82c130028fa942f29add1e0aa0ff9cbd:cffa174304d248b9aa2bdb385d3b01b8@127.0.0.1:9000/6'

# prometheus for monitoring, for more information see
# https://github.com/prometheus/prometheus
# you have to config prometheus first if you want to monitor haipproxy status
EXPORTER_LISTEN_HOST = '0.0.0.0'
EXPORTER_LISTEN_PORT = 55001
