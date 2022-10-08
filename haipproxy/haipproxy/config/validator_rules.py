from haipproxy.config.redis_key import (
    VALIDATOR_HTTP_QUEUE,
    VALIDATOR_HTTPS_QUEUE, VALIDATOR_WALMART_QUEUE
)

# validator
INIT_VALIDATOR_HTTP = 'init'
INIT_VALIDATOR_SOCK4 = 'sock4_init'
INIT_VALIDATOR_SOCK5 = 'sock5_init'

VALIDATOR_HTTP = 'http'
VALIDATOR_HTTPS = 'https'
VALIDATOR_WALMART = 'walmart'

INIT_VALIDATOR_HTTP_TASKS = []
# init validator target website that use https protocol
INIT_VALIDATOR_HTTPS_TASKS = [VALIDATOR_HTTPS, VALIDATOR_WALMART]

# ------------------------------------ EXTRA VALIDATOR ------------------------------------
# validator scheduler will fetch tasks from resource queue and store into task queue
VALIDATOR_TASKS = [
    {
        'name': VALIDATOR_HTTP,
        'task_queue': VALIDATOR_HTTP_QUEUE.TEMP_QUEUE_SET,
        'resource': VALIDATOR_HTTP_QUEUE.SCORE_QUEUE_ZSET,
        'interval': 5,  # 20 minutes
        'enable': 1,
    },
    {
        'name': VALIDATOR_HTTPS,
        'task_queue': VALIDATOR_HTTPS_QUEUE.TEMP_QUEUE_SET,
        'resource': VALIDATOR_HTTPS_QUEUE.SCORE_QUEUE_ZSET,
        'interval': 5,
        'enable': 1,
    },
    {
        'name': VALIDATOR_WALMART,
        'task_queue': VALIDATOR_WALMART_QUEUE.TEMP_QUEUE_SET,
        'resource': VALIDATOR_WALMART_QUEUE.SCORE_QUEUE_ZSET,
        'interval': 5,
        'enable': 1,
    },
]
