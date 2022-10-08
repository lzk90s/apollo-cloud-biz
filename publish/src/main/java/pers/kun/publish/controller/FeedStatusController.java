package pers.kun.publish.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.rest.R;
import pers.kun.core.web.model.PageData;
import pers.kun.publish.dao.entity.FeedStatusDO;
import pers.kun.publish.model.FeedStatusQuery;
import pers.kun.publish.model.FeedStatusVO;
import pers.kun.publish.service.FeedStatusDaoService;
import pers.kun.publish.service.PublishItemDaoService;

import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@RestController
@Slf4j
@RequestMapping("/feed_status")
public class FeedStatusController {
    @Autowired
    private FeedStatusDaoService feedStatusDaoService;
    @Autowired
    private PublishItemDaoService publishItemDaoService;

    @GetMapping
    public R<PageData<FeedStatusVO>> listFeedStatus(FeedStatusQuery req) {
        var entity = new QueryWrapper<FeedStatusDO>();
        entity.eq("tenant_id", UserProvider.getTenantId());
        entity.eq(!StringUtils.isEmpty(req.getPublishConfigId()), "publish_config_id", req.getPublishConfigId());
        entity.eq(req.getStatus() != null, "status", req.getStatus());
        entity.eq(!StringUtils.isEmpty(req.getGoodsId()), "goods_id", req.getGoodsId());
        entity.orderByDesc("add_time");
        var resultDoList = feedStatusDaoService.page(new Page<>(req.getPageNo(),
                req.getPageSize()), entity);
        if (CollectionUtils.isEmpty(resultDoList.getRecords())) {
            return R.ok(new PageData<>(0L, 0L, null));
        }
        var list = resultDoList.getRecords().stream()
                .map(s -> FeedStatusVO.convertFeedStatusEntity().s2t(s))
                .peek(s -> {
                    var publishItem = publishItemDaoService.getById(s.getPublishItemId());
                    s.setPublishData(publishItem.getPublishData());
                })
                .collect(Collectors.toList());
        return R.ok(new PageData<>(resultDoList.getCurrent(), resultDoList.getTotal(), list));
    }
}
