package pers.kun.publish.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.vendor_integration.EcoGoodsFacadeApi;

@FeignClient(name = "goodsSpider", url = "http://vendor:33023", contextId = "goodsSpiderFeign")
public interface EcoGoodsFacadeApiFeign extends EcoGoodsFacadeApi {
}
