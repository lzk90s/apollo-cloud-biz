import click

from reptile import reptile_runner


@click.command()
@click.option('--spider', help='spider to run', required=True)
@click.option('--rule_id', help='reptile rule id', required=True)
@click.option('--rule_opts', help='reptile rule options')
def start(spider, rule_id, rule_opts):
    reptile_runner.run(spider, rule_id, rule_opts)


if __name__ == "__main__":
    start()
