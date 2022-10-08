package pers.kun.publish.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.vendor_integration.TranslateFacadeApi;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
@FeignClient(name = "TranslateApiFeign", url = "http://vendor:33023", contextId = "TranslateApiFeign")
public interface TranslateFacadeApiFeign extends TranslateFacadeApi {
}
