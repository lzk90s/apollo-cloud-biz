package pers.kun.order.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.vendor_integration.OrderFacadeApi;

@FeignClient(name = "orderSpider", url = "${SPIDER_HOST:http://ecommerce-facade:33023}")
public interface OrderFacadeApiFeign extends OrderFacadeApi {
}
