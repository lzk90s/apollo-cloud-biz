from login.taobao.tester_impl import TaoBaoValidTester


class A1688ValidTester(TaoBaoValidTester):
    website = '1688'
    test_url = 'https://work.1688.com/?tracelog=login_target_is_blank_1688'
