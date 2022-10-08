package pers.kun.publish.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.vendor_integration.FenciFacadeApi;

/**
 * @author : qihang.liu
 * @date 2021-09-29
 */
@FeignClient(name = "fenci", url = "http://vendor:33023", contextId = "fenci")
public interface FenciFacadeApiFeign extends FenciFacadeApi {
}
