from flask import request, Response

from util import json_util


def get_auth_from_req():
    client_id = request.args.get("clientId")
    client_secret = request.args.get("clientSecret")
    cookies = request.args.get("cookies")
    if not client_id:
        raise ValueError("No clientId")
    auth = (client_id, client_secret, cookies)
    return auth


def result(r):
    payload = r
    if r and not isinstance(r, str):
        payload = json_util.obj2json(r)
    return Response(payload, content_type="application/json")
