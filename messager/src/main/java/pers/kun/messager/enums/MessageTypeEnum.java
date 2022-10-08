package pers.kun.messager.enums;

import java.util.Arrays;

/**
 * @author : qihang.liu
 * @date 2021-12-06
 */
public enum MessageTypeEnum {
    TEXT("text"),
    MARKDOWN("markdown");

    MessageTypeEnum(String type) {
        this.type = type;
    }

    String getType() {
        return this.type;
    }

    public static MessageTypeEnum getByType(String type) {
        return Arrays.stream(MessageTypeEnum.values()).filter(s -> s.getType().equals(type)).findFirst().orElse(null);
    }

    private String type;
}
