package pers.kun.messager.util;

import com.alibaba.nacos.common.codec.Base64;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.client.RestTemplate;
import pers.kun.core.exception.BizException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author : qihang.liu
 * @date 2021-12-06
 */
public class DingDingUtil {

    @Data
    @AllArgsConstructor
    static class MessageDTO {
        private String msgtype;
        private Text text;
        private Markdown markdown;
    }

    @Data
    @AllArgsConstructor
    static class Text {
        private String content;
    }

    @Data
    @AllArgsConstructor
    static class Markdown {
        private String title;
        private String text;
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
        Response response = restTemplate.postForObject(signUrl(webhookUrl, secret), messageDTO, Response.class);
        if (null != response && response.getErrCode() != 0) {
            throw new BizException("发送消息失败, " + response.getErrMsg());
        }
    }

    public static void sendMarkdownMessage(String webhookUrl, String secret, String text) {
        MessageDTO messageDTO = new MessageDTO("markdown", null, new Markdown("title", text));
        RestTemplate restTemplate = new RestTemplate();
        Response response = restTemplate.postForObject(signUrl(webhookUrl, secret), messageDTO, Response.class);
        if (null != response && response.getErrCode() != 0) {
            throw new BizException("发送消息失败, " + response.getErrMsg());
        }
    }

    private static String sign(Long timestamp, String secret) {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        try {
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (InvalidKeyException | UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 对 url 处理，加签
     *
     * @param url    url
     * @param secret 密钥
     * @return 处理后的请求地址
     */
    private static String signUrl(String url, String secret) {
        long timestamp = System.currentTimeMillis();
        String sign = sign(timestamp, secret);
        return url + "&timestamp=" + timestamp + "&sign=" + sign;
    }
}
