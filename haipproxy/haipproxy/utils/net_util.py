import json
import socket
from concurrent.futures import ThreadPoolExecutor

import requests

executor = ThreadPoolExecutor(3)


def check_opened_port(ip, ports, timeout=5):
    if not ports:
        return []
    futures = []
    for port in ports:
        futures.append(executor.submit(_check_port, ip, port, timeout))
    result = []
    for f in futures:
        port, status = f.result()
        if status:
            result.append(port)
    return result


def _check_port(ip, port, timeout=5):
    sk = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sk.settimeout(timeout)
    is_open = True
    try:
        sk.connect((ip, port))
    except Exception:
        is_open = False
    sk.close()
    return port, is_open


def get_ip_info(ip):
    open_api_url = 'https://api.live.bilibili.com/client/v1/Ip/getInfoNew?ip='
    response = requests.get(open_api_url + ip)
    if response.status_code != 200:
        return None
    result = json.loads(response.text)
    if result['code'] != 0:
        return None
    data = result['data']
    country = data['country']

    return {
        'country': country
    }


if __name__ == "__main__":
    print(json.dumps(get_ip_info('51.81.155.78'), ensure_ascii=False))
