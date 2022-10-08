package pers.kun.messager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Data
@ConfigurationProperties(prefix = "message")
public class MessageProperties {
    private List<MessageRouteInfo> routes;
}
