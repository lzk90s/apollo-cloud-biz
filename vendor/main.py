import logging

from controller.gtin_controller import gtin

logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s')

import flask

from common.exception_advice import *
from controller.currency_controller import *
from controller.fenci_controller import fenci
from controller.product_controller import *
from controller.order_controller import *
from controller.translate_controller import *
from controller.map_controller import *

app = flask.Flask(__name__)


@app.route('/favicon.ico')
def favicon():
    return Response('')


def main():
    app.register_blueprint(exception_advice, url_prefix="/")
    app.register_blueprint(order, url_prefix='/order')
    app.register_blueprint(product, url_prefix='/goods')
    app.register_blueprint(translator, url_prefix='/translator')
    app.register_blueprint(currency, url_prefix='/currency')
    app.register_blueprint(fenci, url_prefix='/fenci')
    app.register_blueprint(gtin, url_prefix='/gtin')
    app.register_blueprint(map, url_prefix='/map')
    app.run(host="0.0.0.0", port=33023, debug=False, threaded=True)


if __name__ == '__main__':
    main()
