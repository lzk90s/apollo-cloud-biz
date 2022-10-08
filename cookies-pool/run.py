import logging

from core.scheduler import Scheduler

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s')


def main():
    s = Scheduler()
    s.run()


if __name__ == '__main__':
    main()
