package pers.kun.publish.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.vendor_integration.GtinFacadeApi;

/**
 * @author : qihang.liu
 * @date 2021-09-29
 */
@FeignClient(name = "gtin", url = "http://vendor:33023", contextId = "gtin")
public interface GtinFacadeApiFeign extends GtinFacadeApi {
}
