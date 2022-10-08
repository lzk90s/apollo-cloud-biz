FROM binacslee/novnc-base:latest as base

RUN echo "https://mirrors.ustc.edu.cn/alpine/v3.13/main" > /etc/apk/repositories && \
    echo "https://mirrors.ustc.edu.cn/alpine/v3.13/community" >> /etc/apk/repositories && \
    echo "https://mirrors.aliyun.com/alpine/edge/testing/" >> /etc/apk/repositories && \
    apk update && \
    apk add --no-cache py3-pip chromium gtk4.0 tzdata wqy-zenhei nginx && \
    rm -f /etc/localtime && ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime


ENV HOME=/home/appuser \
    USER=appuser \
    GROUP=appgroup \
    WORK_DIR=/home/appuser/app

RUN mkdir -p $HOME $WORK_DIR && \
    addgroup -S $GROUP && \
    adduser -S $USER -G $GROUP -s /bin/bash && \
    chown -R $USER:$GROUP /etc/ /tmp /var /run /usr/share $HOME

USER $USER


ADD requirements.txt /tmp/

RUN \
    echo -e "[program:nginx] \n command=nginx -g 'daemon off;' \n autorestart=true \n" >>/etc/supervisord.conf && \
    echo -e "[program:app] \n command=/usr/bin/python3 /home/appuser/app/main.py \n priority=999 \n autorestart=true" >>/etc/supervisord.conf && \
    mkdir -p /run/nginx && \
    rm -f /usr/share/novnc/index.html && \
    ln -s /usr/share/novnc/vnc.html /usr/share/novnc/index.html && \
    pip3 install -r /tmp/requirements.txt --user -i http://mirrors.aliyun.com/pypi/simple/ --trusted-host mirrors.aliyun.com

WORKDIR $WORK_DIR

COPY chrome_ws_proxy.conf /etc/nginx/conf.d/
COPY startup.sh /
ADD --chown=$USER:$GROUP . .

env LC_ALL="en_US.UTF-8" \
    PYTHONIOENCODING=utf-8 \
    IS_DOCKER=1


ENTRYPOINT ["sh", "/startup.sh"]
