FROM python:3.8.12-slim-buster as base

ENV LC_ALL C.UTF-8
ENV LANG C.UTF-8
ENV ISDOCKER 1
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list && \
  sed -i 's/security.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list  && \
  apt-get update && \
  apt-get install -y squid  && \
  rm -rf /var/cache/* && \
  rm -rf /root/.cache/*

RUN sed -i 's/http_access deny all/http_access allow all/g' /etc/squid/squid.conf && cp /etc/squid/squid.conf /etc/squid/squid.conf.backup

ADD requirements.txt /tmp
RUN pip3 install -r /tmp/requirements.txt -i http://mirrors.aliyun.com/pypi/simple/ --trusted-host mirrors.aliyun.com

COPY . /app
WORKDIR /app

CMD ["bash", "/app/startup.sh"]