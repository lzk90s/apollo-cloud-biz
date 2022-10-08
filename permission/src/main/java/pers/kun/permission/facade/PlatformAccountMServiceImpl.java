package pers.kun.permission.facade;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.permission.dao.entity.PlatformAccountDO;
import pers.kun.permission.service.PlatformAccountDaoService;
import pers.kun.core.convert.BeanConverter;
import pers.kun.internal.client.permission.PlatformAccountMService;
import pers.kun.internal.client.permission.PlatformAccountDTO;

import java.util.List;

@RestController
@RequestMapping("/internal/platform_account")
public class PlatformAccountMServiceImpl implements PlatformAccountMService {
    @Autowired
    private PlatformAccountDaoService platformAccountDaoService;

    @Override
    public List<PlatformAccountDTO> listPlatformAccountByTenant(String tenantId) {
        var list = platformAccountDaoService.list(new QueryWrapper<PlatformAccountDO>()
                .eq("tenant_id", tenantId));
        return convertPlatformAccountDTO().s2t(list);
    }

    @Override
    public List<PlatformAccountDTO> listAllUserPlatformAccount() {
        return convertPlatformAccountDTO().s2t(platformAccountDaoService.list());
    }

    @Override
    public PlatformAccountDTO getTenantPlatformAccount(String tenantId, String platformAccount) {
        return listPlatformAccountByTenant(tenantId).stream()
                .filter(s -> s.getName().equals(platformAccount))
                .findFirst()
                .orElse(null);
    }


    public BeanConverter<PlatformAccountDO, PlatformAccountDTO> convertPlatformAccountDTO() {
        return BeanConverter.of(PlatformAccountDO.class, PlatformAccountDTO.class);
    }
}
