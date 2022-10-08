package pers.kun.messager.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import pers.kun.core.util.JsonUtil;
import pers.kun.messager.config.MessageProperties;
import pers.kun.messager.config.MessageRouteInfo;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Service
@Slf4j
@EnableConfigurationProperties(MessageProperties.class)
public class MessageRouter {
    @Autowired
    private MessageProperties messageProperties;
    @Autowired
    private NacosConfigRouteAdapter nacosConfigRouteAdapter;

    @PostConstruct
    public void init() {
        log.info("Message routes is {}", JsonUtil.obj2json(getRoutes()));
    }

    public MessageRouteInfo getMessageRoute(String topic) {
        return getRoutes().stream()
                .filter(s -> Objects.equals(s.getTopic(), topic))
                .findFirst()
                .orElse(null);
    }

    private List<MessageRouteInfo> getRoutes() {
        try {
            return nacosConfigRouteAdapter.getRoutes();
        } catch (Exception e) {
            log.warn("Fetch routes from nacos failed, fallback to local routes");
            return messageProperties.getRoutes();
        }
    }
}
