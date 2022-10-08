FROM opencvcourses/opencv-docker:latest as base

USER root

WORKDIR /root/app

RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update -y && \
    export DEBIAN_FRONTEND=noninteractive && \
    apt-get install -y python3-pip tzdata && \
    rm /etc/localtime && ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    rm -rf /var/lib/apt/lists/* && apt-get clean

ADD requirements.txt /tmp

RUN pip3 install -r /tmp/requirements.txt -i http://mirrors.aliyun.com/pypi/simple/ --trusted-host mirrors.aliyun.com


ADD . .

env LC_ALL="en_US.UTF-8" \
    PYTHONIOENCODING=utf-8

CMD ["python3", "/root/app/main.py"]
