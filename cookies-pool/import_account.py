import click

from core.db import RedisClient


def save_account(conn, user, password):
    result = conn.set(user, password)
    print('账号', user, '密码', password)
    print('录入成功' if result else '录入失败')


@click.command()
@click.option("-w", "--website", help="website")
@click.option("-u", "--user", help="user name")
@click.option("-p", "--password", help="password")
def proc(website, user, password):
    conn = RedisClient('accounts', website)
    save_account(conn, user, password)


if __name__ == '__main__':
    proc()
