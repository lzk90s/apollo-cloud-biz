from urllib.parse import urlparse, urljoin

from scrapy import Request
from w3lib.url import safe_url_string

from haipproxy import crawler_logger
from haipproxy.config.redis_key import VALIDATOR_WALMART_QUEUE
from haipproxy.crawler.redis_spiders import ValidatorRedisSpider
from haipproxy.crawler.validators.base import BaseValidator


class WalmartValidator(BaseValidator, ValidatorRedisSpider):
    """This validator checks the liveness of https proxy resources"""
    name = 'walmart'
    task_queue = VALIDATOR_WALMART_QUEUE.TEMP_QUEUE_SET
    custom_settings = {
        **BaseValidator.custom_settings,
        'HTTPERROR_ALLOWED_CODES': [301, 302, 307],
        'REDIRECT_ENABLED': False,
    }
    urls = [
        'https://www.walmart.com/sw-check.js',
    ]
    success_key = 'walmart'

    def parse(self, response):
        # check 301, 302 status
        status_code = int(response.status)
        if status_code in [301, 302, 307]:
            proxy = response.meta.get('proxy')
            redirection_url = self.get_redirection_url(response)
            crawler_logger.warning("response {}, proxy {}, redirect url {}".format(status_code, proxy, redirection_url))
            if status_code not in [307]:
                return self.redirect(redirection_url, proxy)
        return super().parse(response)

    def redirect(self, url, proxy_url):
        crawler_logger.info('Redirecting {} with proxy {}'.format(url, proxy_url))
        yield Request(url, meta={'proxy': proxy_url}, callback=self.parse, errback=self.parse_error)

    @classmethod
    def get_redirection_url(cls, response):
        location = safe_url_string(response.headers['Location'])
        if response.headers['Location'].startswith(b'//'):
            request_scheme = urlparse(response.url).scheme
            location = request_scheme + '://' + location.lstrip('/')
        return urljoin(response.url, location)
