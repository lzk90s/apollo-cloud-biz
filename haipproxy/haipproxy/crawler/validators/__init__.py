"""
All the spiders are used to validate ip resources.

Here are all the validator websites
https://httpbin.org/ip
http://httpbin.org/ip
https://weibo.cn/

If you want to add your own validators,you must add all the queues
in config/settings.py and register tasks in config/consts.py, and add
the task key to HttpBinInitValidator's https_tasks or http_tasks
"""
from .base import BaseValidator
from .httpbin import (
    HttpBinInitValidator, HttpValidator,
    HttpsValidator
)
from .walmart import WalmartValidator
from ...config.redis_key import (
    INIT_VALIDATOR_HTTP_QUEUE_LIST, INIT_VALIDATOR_SOCK4_QUEUE_LIST,
    INIT_VALIDATOR_SOCK5_QUEUE_LIST, VALIDATOR_HTTP_QUEUE,
    VALIDATOR_HTTPS_QUEUE, VALIDATOR_WALMART_QUEUE
)
from ...config.validator_rules import (
    INIT_VALIDATOR_HTTP, INIT_VALIDATOR_SOCK4, INIT_VALIDATOR_SOCK5, VALIDATOR_HTTP,
    VALIDATOR_HTTPS, VALIDATOR_WALMART
)

ALL_VALIDATORS = [
    HttpBinInitValidator,
    HttpValidator,
    HttpsValidator,
    WalmartValidator
]

ALL_INIT_VALIDATOR_QUEUES = {
    INIT_VALIDATOR_HTTP: INIT_VALIDATOR_HTTP_QUEUE_LIST,
    INIT_VALIDATOR_SOCK4: INIT_VALIDATOR_SOCK4_QUEUE_LIST,
    INIT_VALIDATOR_SOCK5: INIT_VALIDATOR_SOCK5_QUEUE_LIST
}

ALL_VALIDATOR_QUEUES = {
    VALIDATOR_HTTP: VALIDATOR_HTTP_QUEUE,
    VALIDATOR_HTTPS: VALIDATOR_HTTPS_QUEUE,
    VALIDATOR_WALMART: VALIDATOR_WALMART_QUEUE
}

BaseValidator.all_validator_queues = ALL_VALIDATOR_QUEUES
