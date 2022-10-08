package pers.kun.order.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.feign.interceptor.FeignRequestInterceptor;
import pers.kun.internal.client.product.ProductMService;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@FeignClient(value = "product", configuration = FeignRequestInterceptor.class)
public interface ProductMServiceFeign extends ProductMService {
}
