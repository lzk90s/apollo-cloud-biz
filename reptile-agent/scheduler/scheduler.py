import subprocess

import sche
from scrapy import spiderloader
from scrapy.utils.project import get_project_settings

from reptile.client import reptilecontrol_client
from reptile.util import json_util


def get_all_spiders():
    return spiderloader.SpiderLoader.from_settings(get_project_settings()).list()


def start_spider(spider, rule_id, rule_opts):
    print("start spider {} with rule {}".format(spider, rule_opts))
    rule_opts_json = json_util.obj2json(rule_opts, indent=0).replace("\n", "")
    cmd = "python3 reptile_booter.py --spider {} --rule_id {} --rule_opts '{}'".format(spider, rule_id, rule_opts_json)
    return subprocess.call(cmd, shell=True)


def load_all_task():
    spiders = get_all_spiders()
    for spider in spiders:
        rules = reptilecontrol_client.get_reptile_rule(spider)
        if not rules:
            continue
        for rule in rules:
            exec_expr = rule['execExpr']
            sche.when(exec_expr).do(start_spider, spider=spider, rule_id=rule['id'], rule_opts=rule['options'])

    sche.run_forever()
