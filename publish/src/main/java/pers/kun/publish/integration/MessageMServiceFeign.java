package pers.kun.publish.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.feign.interceptor.FeignRequestInterceptor;
import pers.kun.internal.client.messager.MessageMService;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@FeignClient(value = "messager", configuration = FeignRequestInterceptor.class)
public interface MessageMServiceFeign extends MessageMService {

}
