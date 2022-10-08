from haipproxy.config.redis_key import (
    CRAWLER_COMMON_QUEUE_ZSET
)

# crawler
CRAWLER_COMMON = 'common'
CRAWLER_AJAX = 'ajax'
CRAWLER_GFW = 'gfw'
CRAWLER_AJAX_GFW = 'ajax_gfw'

CRAWLER_TASKS = [
    {
        'name': 'mrhinkydink.com',
        'resource': ['http://www.mrhinkydink.com/proxies.htm'],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'css',
            'pre_extract': '.text',
            'infos_pos': 1,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 2 * 60,
        'enable': 1,
    },
    {
        'name': 'ab57.ru',
        'resource': ['http://ab57.ru/downloads/proxyold.txt'],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'text',
        'parse_rule': {
            'pre_extract': None,
            'delimiter': '\r\n',
            'redundancy': None,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'proxylists.net',
        'resource': ['http://www.proxylists.net/http_highanon.txt'],
        'parse_type': 'text',
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_rule': {
            'pre_extract': None,
            'delimiter': '\r\n',
            'redundancy': None,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'my-proxy.com',
        'resource': [
            'https://www.my-proxy.com/free-elite-proxy.html',
            'https://www.my-proxy.com/free-anonymous-proxy.html',
            'https://www.my-proxy.com/free-socks-4-proxy.html',
            'https://www.my-proxy.com/free-socks-5-proxy.html'
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        # if the parse method is specified, set it in the Spider's parser_maps
        'parse_type': 'myproxy',
        'interval': 60,
        'enable': 1,
    },

    {
        'name': 'us-proxy.org',
        'resource': ['https://www.us-proxy.org/'],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'socks-proxy.net',
        'resource': [
            'https://www.socks-proxy.net/',
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'sslproxies.org/',
        'resource': ['https://www.sslproxies.org/'],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'atomintersoft.com',
        'resource': [
            'http://www.atomintersoft.com/high_anonymity_elite_proxy_list',
            'http://www.atomintersoft.com/anonymous_proxy_list',
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tr',
            'infos_pos': 1,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': True,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'geonode.com',
        'resource': [
            'https://geonode.com/free-proxy-list',
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'hidemy.name',
        'resource': [
            'https://hidemy.name/en/proxy-list/?start=%s#list' % (i * 64) for i in range(0, 10)
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'spys.one',
        'resource': [
            'https://advanced.name/freeproxy?page=%s' % i for i in range(1, 5)
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 1,
            'port_pos': 2,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'free-proxy-list.net',
        'resource': [
            'https://free-proxy-list.net/',
            'https://free-proxy-list.net/uk-proxy.html',
            'https://free-proxy-list.net/anonymous-proxy.html',
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'xroxy',
        'resource': ['http://www.xroxy.com/proxylist.php?port=&type=&ssl=&country=&latency=&reliability=&'
                     'sort=reliability&desc=true&pnum=%s#table' % i for i in range(20)],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tbody//tr',
            'infos_pos': 0,
            'infos_end': None,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': False,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    },
    {
        'name': 'proxylistplus',
        'resource': [
            'http://list.proxylistplus.com/Fresh-HTTP-Proxy-List-1',
            'http://list.proxylistplus.com/SSL-List-1'
        ],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'xpath',
            'pre_extract': '//tr[contains(@class, "cells")]',
            'infos_pos': 1,
            'infos_end': -1,
            'detail_rule': 'td::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': False,
            'split_detail': False,
            'protocols': None
        },
        'interval': 3 * 60,
        'enable': 1,
    },
    {
        'name': 'proxy-list.org',
        'resource': ['https://proxy-list.org/english/index.php?p=%s' % i for i in range(1, 11)],
        'task_queue': CRAWLER_COMMON_QUEUE_ZSET,
        'parse_type': 'common',
        'parse_rule': {
            'pre_extract_method': 'css',
            'pre_extract': '.table ul',
            'infos_pos': 1,
            'infos_end': None,
            'detail_rule': 'li::text',
            'ip_pos': 0,
            'port_pos': 1,
            'extract_protocol': True,
            'split_detail': True,
            'protocols': None
        },
        'interval': 60,
        'enable': 1,
    }
]
