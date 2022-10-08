package pers.kun.publish.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.rest.R;
import pers.kun.core.util.JsonUtil;
import pers.kun.core.web.model.PageData;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.publish.dao.entity.PublishItemDO;
import pers.kun.publish.model.PublishItemQuery;
import pers.kun.publish.model.PublishItemVO;
import pers.kun.publish.service.PublishItemDaoService;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@RestController
@Slf4j
@RequestMapping("/publish_item")
public class PublishItemController {
    @Autowired
    private PublishItemDaoService publishItemDaoService;


    @PutMapping("/publish_data/{id}")
    public R<Void> modifyPublishData(@PathVariable("id") Long id, @RequestBody ProductDTO data) {
        log.info("Modify data for publish record {}, {}", id, JsonUtil.obj2json(data));
        PublishItemDO entity = new PublishItemDO();
        entity.setId(id);
        entity.setTenantId(UserProvider.getTenantId());
        entity.setPublishData(data);
        publishItemDaoService.updateById(entity);
        return R.ok();
    }

    @GetMapping
    public R<PageData<PublishItemVO>> listPublishDetailPage(PublishItemQuery req) {
        var entity = new QueryWrapper<PublishItemDO>();
        entity.eq("tenant_id", UserProvider.getTenantId());
        entity.eq(!StringUtils.isEmpty(req.getPublishConfigId()), "publish_config_id", req.getPublishConfigId());
        entity.eq(!StringUtils.isEmpty(req.getAddTime()), "add_time", req.getAddTime());
        entity.eq(req.getStatus() != null, "status", req.getStatus());
        entity.eq(!StringUtils.isEmpty(req.getGoodsId()), "goods_id", req.getGoodsId());
        entity.eq(req.getId() != null, "id", req.getId());
        entity.isNotNull("publish_data");
        entity.ne("publish_data", "");
        entity.orderByDesc("id");
        var resultDoList = publishItemDaoService.page(new Page<>(req.getPageNo(),
                req.getPageSize()), entity);
        if (CollectionUtils.isEmpty(resultDoList.getRecords())) {
            return R.ok(new PageData<>(0L, 0L, null));
        }
        return R.ok(new PageData<>(resultDoList.getCurrent(), resultDoList.getTotal(),
                PublishItemVO.convertPublishItemEntity().s2t(resultDoList.getRecords())));
    }
}
