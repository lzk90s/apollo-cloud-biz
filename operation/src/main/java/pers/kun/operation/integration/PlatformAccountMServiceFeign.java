package pers.kun.operation.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.permission.PlatformAccountMService;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@FeignClient(value = "account")
public interface PlatformAccountMServiceFeign extends PlatformAccountMService {
}
