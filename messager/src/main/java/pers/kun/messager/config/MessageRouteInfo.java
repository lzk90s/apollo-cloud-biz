package pers.kun.messager.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Data
public class MessageRouteInfo {
    /**
     * topic
     */
    @NotBlank
    private String topic;
    /**
     * 类型：wechat, email
     */
    @NotBlank
    private String type;
    /**
     * 消息目的地
     */
    @NotBlank
    private String destination;

    /**
     * 密钥
     */
    private String secret;
}
