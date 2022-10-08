package pers.kun.messager.core.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pers.kun.messager.core.AbstractMessageSender;
import pers.kun.messager.enums.MessageTypeEnum;
import pers.kun.messager.util.QyWechatUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Slf4j
@Service("wechat")
public class WechatMessageSender extends AbstractMessageSender {
    @Override
    protected List<MessageTypeEnum> getMessageType() {
        return Arrays.asList(MessageTypeEnum.TEXT, MessageTypeEnum.MARKDOWN);
    }

    @Override
    protected void sendMarkdownMessage(String destination, String secret, String message) {
        QyWechatUtil.sendMarkdownMessage(destination, secret, message);
    }

    @Override
    protected void sendTextMessage(String destination, String secret, String message) {
        QyWechatUtil.sendTextMessage(destination, secret, message);
    }
}
