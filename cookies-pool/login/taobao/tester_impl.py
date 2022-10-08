import json
import logging

import requests

from core.tester import ValidTester


class TaoBaoValidTester(ValidTester):
    test_url = 'https://i.taobao.com/my_taobao.htm'
    website = 'taobao'