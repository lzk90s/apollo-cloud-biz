package pers.kun.messager.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;
import pers.kun.core.exception.BizException;

/**
 * @author : qihang.liu
 * @date 2021-08-27
 */
public class QyWechatUtil {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Text {
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Markdown {
        private String title;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MessageDTO {
        @JsonProperty("msgtype")
        private String msgType;
        private Text text;
        private Markdown markdown;
    }

    @Data
    static class Response {
        @JsonProperty("errcode")
        private int errCode;
        @JsonProperty("errmsg")
        private String errMsg;
    }

    public static void sendTextMessage(String webhookUrl, String secret, String text) {
        MessageDTO messageDTO = new MessageDTO("text", new Text(text), null);
        RestTemplate restTemplate = new RestTemplate();
        Response response = restTemplate.postForObject(webhookUrl, messageDTO, Response.class);
        if (null != response && response.getErrCode() != 0) {
            throw new BizException("发送微信消息失败, " + response.getErrMsg());
        }
    }

    public static void sendMarkdownMessage(String webhookUrl, String secret, String text) {
        MessageDTO messageDTO = new MessageDTO("markdown", null, new Markdown("title", text));
        RestTemplate restTemplate = new RestTemplate();
        QyWechatUtil.Response response = restTemplate.postForObject(webhookUrl, messageDTO, QyWechatUtil.Response.class);
        if (null != response && response.getErrCode() != 0) {
            throw new BizException("发送消息失败, " + response.getErrMsg());
        }
    }
}
