import platform

# Redis数据库地址
import pyppeteer

REDIS_HOST = 'redis'

# Redis端口
REDIS_PORT = 6379

# Redis密码，如无填None
REDIS_PASSWORD = None

# 产生器类，如扩展其他站点，请在此配置
GENERATOR_MAP = {
    # 'taobao': 'login.taobao.cookies_impl.TaoBaoCookiesGenerator',
    '1688': 'login.1688.cookies_impl.A1688CookiesGenerator',
    'walmart': 'login.walmart.cookies_impl.WalmartCookiesGenerator',
    'googlemail': 'login.googlemail.cookies_impl.GoogleMailCookiesGenerator',
    '51job': 'login.51job.cookies_impl.Job51MailCookiesGenerator'
}

# 测试类，如扩展其他站点，请在此配置
TESTER_MAP = {
    # 'taobao': 'login.taobao.tester_impl.TaoBaoValidTester',
    '1688': 'login.1688.tester_impl.A1688ValidTester',
    'walmart': 'login.walmart.tester_impl.WalmartValidTester',
    'googlemail': 'login.googlemail.tester_impl.GoogleMailValidTester',
    '51job': 'login.51job.tester_impl.Job51MailValidTester'
}

# 产生器和验证器循环周期
CYCLE = 120

# API地址和端口
API_HOST = '0.0.0.0'
API_PORT = 5000

# 产生器开关，模拟登录添加Cookies
GENERATOR_ENABLE = True
# 验证器开关，循环检测数据库中Cookies是否可用，不可用删除
VALID_ENABLE = True
# API接口服务
API_ENABLE = True

## webdriver
PYPPETEER_HEADLESS = True
PYPPETEER_NO_SANDBOX = True
PYPPETEER_DUMPIO = False
PYPPETEER_SINGLE_PROCESS = True

if platform.system() == "Linux":
    PYPPETEER_EXECUTABLE_PATH = "/webdriver-linux/webdriver"
elif platform.system() == "Darwin":
    PYPPETEER_HEADLESS = False
    PYPPETEER_SINGLE_PROCESS = False
    PYPPETEER_EXECUTABLE_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
    REDIS_HOST = "localhost"

pyppeteer.DEBUG = True
