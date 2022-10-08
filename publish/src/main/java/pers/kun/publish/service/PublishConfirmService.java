package pers.kun.publish.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pers.kun.core.util.JsonUtil;
import pers.kun.publish.core.PublishStatusEnum;
import pers.kun.publish.dao.entity.FeedStatusDO;
import pers.kun.publish.dao.entity.PublishItemDO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-10-02
 */
@Slf4j
@Service
public class PublishConfirmService {
    @Autowired
    private PublishItemDaoService publishItemDaoService;
    @Autowired
    private FeedStatusDaoService feedStatusDaoService;
    @Autowired
    private PublishConfigDaoService publishConfigDaoService;

    @Transactional(rollbackFor = Exception.class)
    public void confirm(List<Long> publishItemIdList) {
        if (CollectionUtils.isEmpty(publishItemIdList)) {
            return;
        }
        var list = publishItemDaoService.list(new QueryWrapper<PublishItemDO>()
                .in("id", publishItemIdList)
                .eq("status", PublishStatusEnum.INITIALIZED.getStatus()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 更新状态
        list = list.stream().peek(s -> s.setStatus(PublishStatusEnum.CONFIRMED.getStatus())).collect(Collectors.toList());
        publishItemDaoService.updateBatchById(list);

        // 生成feed记录
        List<FeedStatusDO> feedStatusDOList = new ArrayList<>();
        list.forEach(s -> {
            var publishConfig = publishConfigDaoService.getById(s.getPublishConfigId());
            if (null == publishConfig) {
                return;
            }
            var accounts = publishConfig.getDstPlatformAccount().split(",");
            for (String account : accounts) {
                FeedStatusDO entity = new FeedStatusDO();
                entity.setTenantId(s.getTenantId());
                entity.setStatus(PublishStatusEnum.CONFIRMED.getStatus());
                entity.setGoodsId(s.getGoodsId());
                entity.setPublishConfigId(s.getPublishConfigId());
                entity.setPublishItemId(s.getId());
                entity.setPlatform(publishConfig.getDstPlatform());
                entity.setPlatformAccount(account);
                entity.setTargetGoodsId("-1");
                feedStatusDOList.add(entity);
            }
        });

        log.info("Add feed record, {}", JsonUtil.obj2json(feedStatusDOList));

        // 插入feed记录
        feedStatusDaoService.saveBatch(feedStatusDOList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void ignore(List<Long> publishItemIdList) {
        if (CollectionUtils.isEmpty(publishItemIdList)) {
            return;
        }
        var list = publishItemDaoService.list(new QueryWrapper<PublishItemDO>()
                .in("id", publishItemIdList)
                .eq("status", PublishStatusEnum.INITIALIZED.getStatus()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 更新状态
        list = list.stream().peek(s -> s.setStatus(PublishStatusEnum.IGNORED.getStatus())).collect(Collectors.toList());
        publishItemDaoService.updateBatchById(list);
    }
}
