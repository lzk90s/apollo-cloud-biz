package pers.kun.publish.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.vendor_integration.CurrencyFacadeApi;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
@FeignClient(name = "CurrencyFacadeApiFeign", url = "http://vendor:33023", contextId = "CurrencyFacadeApiFeign")
public interface CurrencyFacadeApiFeign extends CurrencyFacadeApi {
}
