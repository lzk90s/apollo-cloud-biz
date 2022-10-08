"""
Squid Client for spiders.
"""
import os
import subprocess

from .core import IPFetcherMixin
from ..config.settings import (
    SQUID_BIN_PATH, SQUID_CONF_PATH,
    SQUID_TEMPLATE_PATH, TTL_VALIDATED_RESOURCE,
    LONGEST_RESPONSE_TIME, LOWEST_SCORE,
    LOWEST_TOTAL_PROXIES, LOWEST_WEIGHT)
from ..config.validator_rules import (INIT_VALIDATOR_HTTP_TASKS, INIT_VALIDATOR_HTTPS_TASKS)
from ..crawler.validators import ALL_VALIDATOR_QUEUES
# from logger import client_logger
from ..logger import client_logger
from ..utils import get_redis_conn

__all__ = ['SquidClient']


class SquidClient:
    default_conf_detail = "cache_peer {} parent {} 0 no-query weighted-round-robin weight={} " \
                          "connect-fail-limit=4 allow-miss max-conn=5 name=proxy-{}"
    other_confs = ['request_header_access Via deny all', 'request_header_access X-Forwarded-For deny all',
                   'request_header_access From deny all', 'never_direct allow all']

    def __init__(self, tasks, validator_maps=ALL_VALIDATOR_QUEUES,
                 longest_response_time=LONGEST_RESPONSE_TIME, lowest_score=LOWEST_SCORE, lowest_weight=LOWEST_WEIGHT,
                 ttl_validated_resource=TTL_VALIDATED_RESOURCE, min_pool_size=LOWEST_TOTAL_PROXIES):
        if not tasks:
            tasks = INIT_VALIDATOR_HTTP_TASKS + INIT_VALIDATOR_HTTPS_TASKS
        self.ip_fetchers = []
        for task in tasks:
            queue = validator_maps.get(task)
            score_queue = queue.SCORE_QUEUE_ZSET
            weight_queue = queue.WEIGHT_QUEUE_ZSET
            ttl_queue = queue.TTL_QUEUE_ZSET
            speed_queue = queue.SPEED_QUEUE_ZSET
            fetcher = IPFetcherMixin(task, score_queue, weight_queue, ttl_queue, speed_queue, longest_response_time,
                                     lowest_score, lowest_weight, ttl_validated_resource, min_pool_size)
            self.ip_fetchers.append(fetcher)
        self.template_path = SQUID_TEMPLATE_PATH
        self.conf_path = SQUID_CONF_PATH
        if not SQUID_BIN_PATH:
            try:
                r = subprocess.check_output('which squid', shell=True)
                self.squid_path = r.decode().strip()
            except subprocess.CalledProcessError:
                client_logger.warning('no squid is installed on this machine, or the installed dir is not '
                                      'contained in environment path')
                pass
        else:
            self.squid_path = SQUID_BIN_PATH

    @classmethod
    def get_pid(cls):
        pid_file = "/var/run/squid.pid"
        if not os.path.exists(pid_file):
            return None
        with open(pid_file, "r") as f:
            return f.readline().replace("\n", "").strip()

    def check_proc(self):
        if not self.squid_is_alive():
            self.start_squid()

    def squid_is_alive(self):
        pid = self.get_pid()
        if not pid:
            return False
        # client_logger.info("pid is {}".format(pid))
        process_dir = os.path.join('/proc', str(pid))
        return os.path.exists(process_dir)

    def start_squid(self):
        client_logger.info("start squid")
        subprocess.call(self.squid_path + ' -N -d1 &', shell=True)

    def reconfigure_squid(self):
        client_logger.info("reconfigure squid")
        subprocess.call([self.squid_path, '-k', 'reconfigure'], shell=False)

    def update_conf(self):
        proxies = self.get_available_proxies()
        proxies = [p['url'] for p in proxies]
        conts = list()
        with open(self.template_path, 'r') as fr, open(self.conf_path, 'w') as fw:
            original_conf = fr.read()
            if not proxies:
                fw.write(original_conf)
                client_logger.info('no proxies got at this turn')
            else:
                conts.append(original_conf)
                # if two proxies use the same ip and different ports and no name
                # is assigned, cache_peer error will raise.
                for index, proxy in enumerate(proxies):
                    _, ip_port = proxy.split('://')
                    ip, port = ip_port.split(':')
                    conts.append(self.default_conf_detail.format(ip, port, len(proxies) - index, index))
                conts.extend(self.other_confs)
                conf = '\n'.join(conts)
                fw.write(conf)
        # in docker, execute with shell will fail
        self.reconfigure_squid()
        client_logger.info('Squid conf is successfully updated')

    def get_available_proxies(self):
        conn = get_redis_conn()
        proxies = []
        for fetcher in self.ip_fetchers:
            proxies.extend(fetcher.get_available_proxies(conn))
        return proxies
