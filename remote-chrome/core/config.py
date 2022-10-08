## chrome
import os
import platform

import pyppeteer

IS_DOCKER = os.getenv("IS_DOCKER")

API_HOST = '0.0.0.0'
if IS_DOCKER:
    API_PORT = 45001
else:
    API_PORT = 45000

# chrome
PYPPETEER_BROWSER_PAGE_MAX_NUM = 10
PYPPETEER_HEADLESS = False
PYPPETEER_NO_SANDBOX = True
PYPPETEER_DUMPIO = False
PYPPETEER_SINGLE_PROCESS = False

# local port
debugging_port = os.getenv("REMOTE_DEBUGGING_PORT")
PYPPETEER_REMOTE_DEBUGGING_PORT = debugging_port if debugging_port is not None else 45002

PYPPETEER_PROXY = os.getenv("PROXY") if os.getenv("PROXY") else None

if platform.system() == "Linux":
    PYPPETEER_EXECUTABLE_PATH = "/usr/bin/chromium-browser"
elif platform.system() == "Darwin":
    PYPPETEER_EXECUTABLE_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

pyppeteer.DEBUG = True
