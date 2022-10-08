package pers.kun.order.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.uaa.UserMService;

@FeignClient("uaa")
public interface UserApiFeign extends UserMService {
}
