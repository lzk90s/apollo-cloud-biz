# ------------------------------------ all ------------------------------------
# data_all is a set , it's a dupefilter
DATA_ALL_SET = 'haipproxy:all'
PROXY_DETAIL_HASHMAP = 'haipproxy:ipdetail'

# ------------------------------------ scheduler ------------------------------------
TIMER_RECORDER_HASHMAP = 'haipproxy:scheduler:task'
LOCKER_PREFIX = 'haipproxy:lock:'

# ------------------------------------ crawler ------------------------------------
CRAWLER_COUNTER_HASHMAP = 'haipproxy:crawler:counter'
CRAWLER_COMMON_QUEUE_ZSET = 'haipproxy:spider:common'
CRAWLER_AJAX_QUEUE_ZSET = 'haipproxy:spider:ajax'
CRAWLER_GFW_QUEUE_ZSET = 'haipproxy:spider:gfw'
CRAWLER_AJAX_GFW_QUEUE_ZSET = 'haipproxy:spider:ajax_gfw'

# ------------------------------------ init validator ------------------------------------
INIT_VALIDATOR_HTTP_QUEUE_LIST = 'haipproxy:init:http'
INIT_VALIDATOR_SOCK4_QUEUE_LIST = 'haipproxy:init:socks4'
INIT_VALIDATOR_SOCK5_QUEUE_LIST = 'haipproxy:init:socks5'


# ------------------------------------ extra validator ------------------------------------
class ValidatorQueue:
    def __init__(self, type):
        self.TEMP_QUEUE_SET = 'haipproxy:temp:' + type
        self.SCORE_QUEUE_ZSET = 'haipproxy:validated:' + type
        self.WEIGHT_QUEUE_ZSET = 'haipproxy:weight:' + type
        self.TTL_QUEUE_ZSET = 'haipproxy:ttl:' + type
        self.SPEED_QUEUE_ZSET = 'haipproxy:speed:' + type


VALIDATOR_HTTP_QUEUE = ValidatorQueue('http')
VALIDATOR_HTTPS_QUEUE = ValidatorQueue('https')
VALIDATOR_WALMART_QUEUE = ValidatorQueue('walmart')
