package pers.kun.internal.client.messager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    /**
     * 消息topic
     */
    private String topic;
    /**
     * title
     */
    private String title;
    /**
     * 消息类型：text，markdown
     */
    private String messageType;
    /**
     * 消息体
     */
    private String message;
}
