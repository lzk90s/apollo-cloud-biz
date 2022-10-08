"""
web api for haipproxy
"""
import os

from flask import (
    Flask, jsonify as flask_jsonify, Response, request)

from haipproxy.monitor import exporter
from ..client.py_cli import ProxyFetcher
from ..config.validator_rules import VALIDATOR_TASKS


def jsonify(*args, **kwargs):
    response = flask_jsonify(*args, **kwargs)
    if not response.data.endswith(b"\n"):
        response.data += b"\n"
    return response


# web api uses robin strategy for proxy schedule, crawler client may implete
# its own schedule strategy
usage_registry = {task['name']: ProxyFetcher(task['name'], strategy='random') for task in VALIDATOR_TASKS}
app = Flask(__name__)
app.debug = bool(os.environ.get("DEBUG"))
app.config["JSONIFY_PRETTYPRINT_REGULAR"] = True
app.config['JSON_AS_ASCII'] = False
registry = exporter.register()


@app.errorhandler(404)
def not_found(e):
    return jsonify({
        'reason': 'resource not found',
        'status_code': 404
    })


@app.errorhandler(500)
def not_found(e):
    return jsonify({
        'reason': 'internal server error',
        'status_code': 500
    })


@app.route("/metrics")
def metrics():
    return Response(exporter.get_latest_metrics(registry), mimetype="text/plain")


@app.route("/proxy/get/<usage>")
def get_proxy(usage):
    # default usage is 'https'
    if usage not in usage_registry:
        usage = 'https'
    country = request.args.get('country')
    filter_opts = {}
    if country:
        filter_opts['country'] = country
    proxy_fetcher = usage_registry.get(usage)
    ip = proxy_fetcher.get_proxy(filter_opts)
    return jsonify({
        'proxy': ip,
        'resource': usage,
        'status_code': 200
    })


@app.route("/proxy/delete/<usage>/<proxy>")
def delete_proxy(usage, proxy):
    if usage not in usage_registry:
        usage = 'https'
    proxy_fetcher = usage_registry.get(usage)
    proxy_fetcher.delete_proxy(proxy)
    return jsonify({
        'result': 'ok',
        'status_code': 200
    })


@app.route("/pool/get/<usage>")
def get_proxies(usage):
    if usage not in usage_registry:
        usage = 'https'
    country = request.args.get('country')
    filter_opts = {}
    if country:
        filter_opts['country'] = country
    proxy_fetcher = usage_registry.get(usage)
    return jsonify({
        'pool': proxy_fetcher.filter_with_opts(filter_opts),
        'resource': usage,
        'status_code': 200
    })
