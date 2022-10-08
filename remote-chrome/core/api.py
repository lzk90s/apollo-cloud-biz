from flask import Flask, request

__all__ = ['app', 'chrome_runner']

from core import config
from browser.chrome_runner import ChromeRunner

app = Flask(__name__)
chrome_runner = ChromeRunner()


@app.route('/browser/ws_endpoint', methods=['GET'])
def get_browser_endpoint():
    endpoint = str(chrome_runner.get_ws_endpoint())
    url = endpoint if not config.IS_DOCKER else endpoint[endpoint.index("/devtools"):]
    return url


@app.route('/browser/alive', methods=['GET'])
def get_browser_version():
    return str(chrome_runner.is_alive())


@app.route("/browser/restart", methods=['POST'])
def restart_browser():
    data = request.json
    proxy = None
    if data and 'proxy' in data:
        proxy = data['proxy']
    chrome_runner.restart(proxy)
    return "ok"


if __name__ == '__main__':
    app.run(host='0.0.0.0')
