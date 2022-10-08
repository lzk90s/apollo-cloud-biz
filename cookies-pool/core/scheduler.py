import logging
import threading
import time

from core.api import app
from core.generator import CookiesGenerator
from core.tester import *


def load_class_by_name(class_name):
    module_name = "."
    if class_name.rfind(".") > 0:
        module_name = class_name[0:class_name.rfind(".")]
        class_name = class_name[class_name.rfind(".") + 1:]
    try:
        m = __import__(module_name, fromlist=True)
        return getattr(m, class_name)
    except Exception as e:
        logging.error("Load class by name failed, class_name {}, {}".format(class_name, e.args))
        raise e


class Scheduler(object):

    @staticmethod
    def valid_cookie(cycle=CYCLE):
        while True:
            logging.info('Cookies检测进程开始运行')
            try:
                for website, cls in TESTER_MAP.items():
                    tester = load_class_by_name(cls)()
                    assert isinstance(tester, ValidTester)
                    logging.info("Run {} cookies validator".format(tester.website))
                    tester.run()
                    logging.info('Cookies检测完成')
                    del tester
                time.sleep(cycle)
            except Exception as e:
                print(e.args)
                time.sleep(cycle)

    @staticmethod
    def generate_cookie(cycle=CYCLE):
        while True:
            logging.info('Cookies生成进程开始运行')
            try:
                for website, cls in GENERATOR_MAP.items():
                    generator = load_class_by_name(cls)()
                    assert isinstance(generator, CookiesGenerator)
                    logging.info("Run {} cookies generator".format(generator.website))
                    generator.run()
                    logging.info('Cookies生成完成')
                    generator.close()
                    del generator
                time.sleep(cycle)
            except Exception as e:
                print(e.args)
                time.sleep(cycle)

    @staticmethod
    def api():
        logging.info('API接口开始运行')
        app.run(host=API_HOST, port=API_PORT)

    def run(self):
        if API_ENABLE:
            api_process = threading.Thread(target=Scheduler.api)
            api_process.start()

        if VALID_ENABLE:
            valid_process = threading.Thread(target=Scheduler.valid_cookie)
            valid_process.start()
            time.sleep(2)

        if GENERATOR_ENABLE:
            Scheduler.generate_cookie()
