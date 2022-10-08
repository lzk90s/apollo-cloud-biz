package pers.kun.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.kun.permission.dao.entity.PlatformAccountDO;
import pers.kun.permission.model.PlatformAccountQuery;
import pers.kun.permission.model.PlatformAccountVO;
import pers.kun.permission.service.PlatformAccountDaoService;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.rest.R;
import pers.kun.core.web.model.PageData;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/platform_account")
public class PlatformAccountController {
    @Autowired
    private PlatformAccountDaoService platformAccountDaoService;

    @PostMapping
    public R<Void> addAccount(@RequestBody PlatformAccountDO platformAccountEntity) {
        platformAccountEntity.setId(null);
        // no password
        platformAccountEntity.setPlatformPassword("***");
        platformAccountEntity.setTenantId(UserProvider.getUserName());
        platformAccountDaoService.save(platformAccountEntity);
        return R.ok();
    }

    @PutMapping
    public R<Void> updateAccount(@RequestBody PlatformAccountDO platformAccountEntity) {
        platformAccountEntity.setTenantId(UserProvider.getUserName());
        platformAccountDaoService.saveOrUpdate(platformAccountEntity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteAccount(@PathVariable Long id) {
        platformAccountDaoService.removeById(id);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<PlatformAccountDO> getAccount(@PathVariable Long id) {
        return R.ok(platformAccountDaoService.getById(id));
    }

    @GetMapping
    public R<PageData<PlatformAccountVO>> listAccount(PlatformAccountQuery req) {
        var entityWrapper = new QueryWrapper<PlatformAccountDO>()
                .eq("tenant_id", UserProvider.getTenantId());
        var page = platformAccountDaoService.page(new Page<>(req.getPageNo(), req.getPageSize()), entityWrapper);
        var list = page.getRecords().stream()
                .map(s -> PlatformAccountVO.convertPlatformAccountEntity().s2t(s))
                .peek(s -> s.setPlatformPassword("******")).collect(Collectors.toList());
        return R.ok(new PageData<>(page.getCurrent(), page.getTotal(), list));
    }
}
