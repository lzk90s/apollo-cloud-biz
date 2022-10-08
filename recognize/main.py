import logging

import flask

from api import recognition
from common.exception_advice import exception_advice
from config import API_HOST, API_PORT

app = flask.Flask(__name__)

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s')


def main():
    app.register_blueprint(exception_advice, url_prefix="/")
    app.register_blueprint(recognition, url_prefix='/recognition')
    app.run(host=API_HOST, port=API_PORT, debug=False, threaded=True)


if __name__ == '__main__':
    main()
