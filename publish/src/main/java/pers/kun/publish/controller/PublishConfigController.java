package pers.kun.publish.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.exception.NoPermissionException;
import pers.kun.core.rest.R;
import pers.kun.core.web.model.PageData;
import pers.kun.publish.dao.entity.PublishConfigDO;
import pers.kun.publish.model.PublishConfigAddCmd;
import pers.kun.publish.model.PublishConfigModifyCmd;
import pers.kun.publish.model.PublishConfigQuery;
import pers.kun.publish.model.PublishConfigVO;
import pers.kun.publish.service.PublishConfigDaoService;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author : qihang.liu
 * @date 2021-06-12
 */
@RestController
@RequestMapping("/publish_config")
public class PublishConfigController {
    @Autowired
    private PublishConfigDaoService publishConfigDaoService;

    @PostMapping
    public R<Long> addConfig(@RequestBody PublishConfigAddCmd request) {
        var entity = PublishConfigAddCmd.convertPublishConfigEntity().s2t(request);
        entity.setStatus(1);
        entity.setTenantId(UserProvider.getTenantId());
        publishConfigDaoService.save(entity);
        return R.ok(entity.getId());
    }

    @PutMapping
    public R<Void> modifyConfig(@RequestBody PublishConfigModifyCmd request) {
        var entity = PublishConfigModifyCmd.convertPublishConfigEntity().s2t(request);
        entity.setTenantId(UserProvider.getTenantId());
        publishConfigDaoService.updateById(entity);
        return R.ok();
    }

    @PutMapping("/status/{id}")
    public R<Void> enableConfig(@PathVariable("id") Long id, @RequestParam Integer status) {
        var entity = new PublishConfigDO();
        entity.setId(id);
        entity.setStatus(status);
        entity.setTenantId(UserProvider.getTenantId());
        publishConfigDaoService.updateById(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteConfig(@PathVariable("id") @NotNull Long id) {
        var record = publishConfigDaoService.getById(id);
        if (!record.getTenantId().equals(UserProvider.getTenantId())) {
            throw new NoPermissionException();
        }
        publishConfigDaoService.removeById(id);
        return R.ok();
    }

    @GetMapping
    public R<PageData<PublishConfigVO>> listPublishConfig(PublishConfigQuery req) {
        var entity = new QueryWrapper<PublishConfigDO>();
        entity.eq("tenant_id", UserProvider.getTenantId());
        Optional.ofNullable(req.getName()).ifPresent(s -> entity.like("name", s));
        var resultDo = publishConfigDaoService.page(new Page<>(req.getPageNo(), req.getPageSize()), entity);
        if (CollectionUtils.isEmpty(resultDo.getRecords())) {
            return R.ok(new PageData<>(0L, 0L, null));
        }
        return R.ok(new PageData<>(resultDo.getCurrent(), resultDo.getTotal(),
                PublishConfigVO.convertPublishConfigEntity().s2t(resultDo.getRecords())));
    }
}
