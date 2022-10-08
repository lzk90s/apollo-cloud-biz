import os

os.environ.setdefault('SCRAPY_PROJECT', 'reptile')
os.environ.setdefault('SCRAPY_SETTINGS_MODULE', 'reptile.settings')
from scrapy.crawler import CrawlerProcess
from scrapy.utils.project import get_project_settings


def run(spider, rule_id, rule_opts):
    process = CrawlerProcess(get_project_settings())
    process.crawl(spider, rule_id=rule_id, rule_opts=rule_opts)
    process.start()
