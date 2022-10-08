package pers.kun.messager.core;

import lombok.extern.slf4j.Slf4j;
import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.messager.config.MessageRouteInfo;
import pers.kun.messager.enums.MessageTypeEnum;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-12-06
 */
@Slf4j
public abstract class AbstractMessageSender implements MessageSender {
    @Override
    public void send(MessageRouteInfo route, MessageDTO message) {
        // 判断是否支持
        var msgType = MessageTypeEnum.getByType(message.getMessageType());
        if (!getMessageType().contains(msgType)) {
            throw new UnsupportedOperationException();
        }

        try {
            send(route.getDestination(), route.getSecret(), msgType, message);
            log.info("Send message succeed, topic={}, type={}, destination={}, message=\n{}",
                    route.getTopic(), route.getType(), route.getDestination(), message.getMessage());
        } catch (Exception e) {
            log.info("Send message to {} failed, message={}, reason={}", route.getType(), message, e.getMessage());
        }
    }

    protected abstract List<MessageTypeEnum> getMessageType();

    protected abstract void sendMarkdownMessage(String destination, String secret, String message);

    protected abstract void sendTextMessage(String destination, String secret, String message);

    private void send(String destination, String secret, MessageTypeEnum messageType, MessageDTO message) {
        switch (messageType) {
            case TEXT:
                sendTextMessage(destination, secret, message.getMessage());
                break;
            case MARKDOWN:
                sendMarkdownMessage(destination, secret, message.getMessage());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
