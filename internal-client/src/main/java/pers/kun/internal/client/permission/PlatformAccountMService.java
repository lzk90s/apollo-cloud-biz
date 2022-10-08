package pers.kun.internal.client.permission;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/internal/platform_account")
public interface PlatformAccountMService {
    @GetMapping("/list_platform_account_by_tenant")
    List<PlatformAccountDTO> listPlatformAccountByTenant(@RequestParam("tenantId") String tenantId);

    @GetMapping("/list_all_user_platform_account")
    List<PlatformAccountDTO> listAllUserPlatformAccount();

    @GetMapping("/get_user_platform_account")
    PlatformAccountDTO getTenantPlatformAccount(@RequestParam("tenantId") String tenantId,
                                                @RequestParam("platformAccount") String platformAccount);
}
