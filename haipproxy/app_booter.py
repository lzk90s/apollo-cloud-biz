"""
This module is used to provide web api for other languages
"""
import argparse

from haipproxy.api import app
from haipproxy.config.settings import API_LISTEN_PORT, API_LISTEN_HOST

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", type=int, default=API_LISTEN_PORT)
    parser.add_argument("--host", default=API_LISTEN_HOST)
    args = parser.parse_args()
    app.run(port=args.port, host=args.host)
