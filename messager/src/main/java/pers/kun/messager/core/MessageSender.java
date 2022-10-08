package pers.kun.messager.core;

import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.messager.config.MessageRouteInfo;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
public interface MessageSender {
    void send(MessageRouteInfo route, MessageDTO message);
}
