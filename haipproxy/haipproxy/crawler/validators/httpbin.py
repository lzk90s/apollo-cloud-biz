"""
We use this validator to filter transparent ips, and give the ip resources an
initial score.
"""
import json
import time
from json.decoder import JSONDecodeError

import requests

from haipproxy.config.settings import (ORIGIN_IP)
from haipproxy.config.validator_rules import (
    INIT_VALIDATOR_HTTP_TASKS, INIT_VALIDATOR_HTTPS_TASKS)
from .base import BaseValidator
from ..items import (
    ProxyScore, ProxyVerifiedTime,
    ProxySpeed, ProxyWeight, ProxyValidateItem)
from ..redis_spiders import ValidatorRedisSpider
from ...config.redis_key import INIT_VALIDATOR_HTTP_QUEUE_LIST, VALIDATOR_HTTP_QUEUE, VALIDATOR_HTTPS_QUEUE


class HttpBinInitValidator(BaseValidator, ValidatorRedisSpider):
    """This validator does initial work for ip resources.
    　　It will filter transparent ip and store proxies in http_task
       and https_tasks
    """
    name = 'init'
    urls = [
        'http://httpbin.org/ip',
        'https://httpbin.org/ip',
    ]
    use_set = False
    task_queue = INIT_VALIDATOR_HTTP_QUEUE_LIST
    # https_tasks = ['https']
    # distribute proxies to each queue, according to
    # VALIDORTOR_TASKS in consts.py
    https_tasks = INIT_VALIDATOR_HTTPS_TASKS
    http_tasks = INIT_VALIDATOR_HTTP_TASKS

    def __init__(self):
        super().__init__()
        if ORIGIN_IP:
            self.origin_ip = ORIGIN_IP
        else:
            self.origin_ip = requests.get(self.urls[1]).json().get('origin')

    def is_transparent(self, response):
        """filter transparent ip resources"""
        if not response.text:
            return True
        try:
            ip = json.loads(response.text).get('origin')
            if self.origin_ip in ip:
                return True
        except (AttributeError, JSONDecodeError):
            return True

        return False

    def set_item_queue(self, url, proxy, score, score_incr, speed=0, weight=0) -> list:
        items = list()
        tasks = self.https_tasks if 'https' in url else self.http_tasks
        # todo set proxy to tmp_queue
        for task in tasks:
            queue = self.all_validator_queues.get(task)
            score_item = ProxyScore(queue.SCORE_QUEUE_ZSET, score)
            weight_item = ProxyWeight(queue.WEIGHT_QUEUE_ZSET, weight)
            ttl_item = ProxyVerifiedTime(queue.TTL_QUEUE_ZSET, int(time.time()))
            speed_item = ProxySpeed(queue.SPEED_QUEUE_ZSET, speed)
            item = ProxyValidateItem(url=proxy,
                                     incr=score_incr,
                                     score_dimension=score_item,
                                     weight_dimension=weight_item,
                                     verified_time_dimension=ttl_item,
                                     speed_dimension=speed_item)
            items.append(item)
        return items


class HttpValidator(BaseValidator, ValidatorRedisSpider):
    """This validator checks the liveness of http proxy resources"""
    name = 'http'
    task_queue = VALIDATOR_HTTP_QUEUE.TEMP_QUEUE_SET
    urls = [
        'http://httpbin.org/ip',
    ]


class HttpsValidator(BaseValidator, ValidatorRedisSpider):
    """This validator checks the liveness of https proxy resources"""
    name = 'https'
    task_queue = VALIDATOR_HTTPS_QUEUE.TEMP_QUEUE_SET
    urls = [
        'https://httpbin.org/ip',
    ]
