from core.tester import ValidTester, BaseValidTester


class GoogleMailValidTester(BaseValidTester):
    website = '51job'
    test_url = 'https://mail.google.com/mail/u/0/#inbox'