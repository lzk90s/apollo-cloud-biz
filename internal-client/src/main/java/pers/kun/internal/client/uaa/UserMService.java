package pers.kun.internal.client.uaa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/internal/user")
public interface UserMService {
    @GetMapping("/info/tenant")
    UserInfoDTO getByTenant(@RequestParam("tenantId") String tenantId);

    @GetMapping("/info/username")
    UserInfoDTO getByUserName(@RequestParam("userName") String userName);
}
