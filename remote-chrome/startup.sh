#!/bin/sh

HOME=/home/appuser

if [ -n "$VNC_PASSWORD" ]; then
    echo -n "$VNC_PASSWORD" > $HOME/.password1
    x11vnc -storepasswd $(cat $HOME/.password1) $HOME/.password2
    sed -i 's/^command=\/usr\/bin\/x11vnc.*/& -xkb -forever -shared -repeat -capslock -rfbauth \/home\/appuser\/.password2/' /etc/supervisord.conf
    export VNC_PASSWORD=
fi

supervisord -c /etc/supervisord.conf