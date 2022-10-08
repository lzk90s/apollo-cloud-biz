#!/bin/bash
mkdir -p logs
rm -rf /var/run/squid.pid

nohup python crawler_booter.py --usage crawler common > logs/crawler.log 2>&1 &
nohup python scheduler_booter.py --usage crawler common > logs/crawler_scheduler.log 2>&1 &
nohup python crawler_booter.py --usage validator init > logs/init_validator1.log 2>&1 &
nohup python crawler_booter.py --usage validator init > logs/init_validator2.log 2>&1 &
nohup python crawler_booter.py --usage validator https https > logs/https_validator.log 2>&1&
nohup python crawler_booter.py --usage validator walmart walmart > logs/walmart_validator.log 2>&1&
nohup python scheduler_booter.py --usage validator https walmart > logs/validator_scheduler.log 2>&1 &
nohup python squid_update.py --interval 2 --check_squid walmart > logs/squid_update.log 2>&1 &
python app_booter.py
