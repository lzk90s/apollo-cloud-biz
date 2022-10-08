package pers.kun.messager.core;

import pers.kun.core.util.SpringContextHolder;

/**
 * @author : qihang.liu
 * @date 2021-12-06
 */
public class MessageSenderSelector {
    public static MessageSender select(String type) {
        return SpringContextHolder.getBean(type);
    }
}
