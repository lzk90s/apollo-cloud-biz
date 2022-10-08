package pers.kun.operation.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.feign.interceptor.FeignRequestInterceptor;
import pers.kun.internal.client.reptile_control.ReptileRuleMService;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@FeignClient(value = "goods", configuration = FeignRequestInterceptor.class)
public interface ReptileRuleMServiceFeign extends ReptileRuleMService {
}
