package pers.kun.messager.core;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pers.kun.core.util.JsonUtil;
import pers.kun.messager.config.MessageRouteInfo;

import java.util.Collections;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2022-01-11
 */
@Component
public class NacosConfigRouteAdapter {
    @Value("${dataId:messager-router}")
    private String dataId;
    @Value("${group:DEFAULT_GROUP}")
    private String group;
    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddress;

    public List<MessageRouteInfo> getRoutes() {
        String data = getRestTemplate().getForObject(buildUrl(), String.class);
        if (StringUtils.isBlank(data)) {
            return Collections.emptyList();
        }
        return JsonUtil.json2list(data, MessageRouteInfo.class);
    }

    public RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(2000);
        httpRequestFactory.setConnectTimeout(2000);
        httpRequestFactory.setReadTimeout(2000);
        return new RestTemplate(httpRequestFactory);
    }

    private String buildUrl() {
        return String.format("http://%s/nacos/v1/cs/configs?dataId=%s&group=%s", serverAddress, dataId, group);
    }
}
