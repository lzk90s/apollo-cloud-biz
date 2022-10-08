package pers.kun.messager.core.support;

import org.springframework.stereotype.Service;
import pers.kun.messager.core.AbstractMessageSender;
import pers.kun.messager.enums.MessageTypeEnum;
import pers.kun.messager.util.DingDingUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-12-06
 */
@Service("dingding")
public class DingDingMessageSender extends AbstractMessageSender {
    @Override
    protected List<MessageTypeEnum> getMessageType() {
        return Arrays.asList(MessageTypeEnum.TEXT, MessageTypeEnum.MARKDOWN);
    }

    @Override
    protected void sendMarkdownMessage(String destination, String secret, String message) {
        DingDingUtil.sendMarkdownMessage(destination, secret, message);
    }

    @Override
    protected void sendTextMessage(String destination, String secret, String message) {
        DingDingUtil.sendTextMessage(destination, secret, message);
    }
}
