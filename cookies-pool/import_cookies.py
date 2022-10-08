import click

from core.db import RedisClient


def set(conn, account, cookies):
    result = conn.set(account, cookies)
    print('录入成功' if result else '录入失败')


def import_cookies(conn, account, file):
    with open(file, "r") as f:
        buf = f.read()
        set(conn, account, buf)


@click.command()
@click.option("-f", "--file", default="cookies.json", help="Cookies file")
@click.option("-a", "--account", help="Account for this cookies")
@click.option("-w", "--website", help="Website for this cookies")
def proc(website, account, file):
    conn = RedisClient('cookies', website)
    import_cookies(conn, account, file)


if __name__ == '__main__':
    proc()
