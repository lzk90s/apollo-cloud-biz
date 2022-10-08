package pers.kun.publish.core.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pers.kun.core.exception.BizException;
import pers.kun.internal.client.vendor_integration.UploadStatusDTO;
import pers.kun.publish.core.PublishStatusEnum;
import pers.kun.publish.dao.entity.FeedResult;
import pers.kun.publish.dao.entity.FeedStatusDO;
import pers.kun.publish.dao.entity.PublishItemDO;
import pers.kun.publish.service.PublishItemDaoService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
@Slf4j
@Component("defaultPublisherImpl")
public class DefaultFeedImpl extends FeedLifeCycle {
    private static final String SEPARATOR = ",";

    @Autowired
    private PublishItemDaoService publishItemDaoService;

    @Override
    protected void doUpload(List<FeedStatusDO> feedStatusList) {
        var ids = feedStatusList.stream().map(FeedStatusDO::getPublishItemId).collect(Collectors.toList());
        var publishItemList = publishItemDaoService.listByIds(ids);
        var uploadGoodsList = publishItemList.stream().map(PublishItemDO::getPublishData).collect(Collectors.toList());

        // 获取上传账号信息，只取第一个，因为都是一样的
        FeedStatusDO tmpFeed = feedStatusList.get(0);
        String platform = tmpFeed.getPlatform();
        String tenantId = tmpFeed.getTenantId();
        String platformAccount = tmpFeed.getPlatformAccount();
        var account = platformAccountApiFeign.getTenantPlatformAccount(tenantId, platformAccount);
        Optional.ofNullable(account).orElseThrow(() -> new BizException("Invalid account"));

        // 批量上传
        String uploadId = ecoGoodsFacadeApiFeign.uploadProduct(platform, account.getClientId(), account.getClientSecret(), uploadGoodsList);

        // 更新feed result id和status
        feedStatusList = feedStatusList.stream().peek(s -> {
            s.setFeedResultId(uploadId);
            s.setStatus(PublishStatusEnum.UPLOADING.getStatus());
        }).collect(Collectors.toList());
        feedStatusDaoService.updateBatchById(feedStatusList);
    }

    @Override
    protected void doSyncUploadStatus(List<FeedStatusDO> feedStatusList) {
        // 检查商品上架进度
        Map<String, UploadStatusDTO> uploadStatusCache = new HashMap<>();
        feedStatusList.stream().filter(s -> !StringUtils.isEmpty(s.getFeedResultId())).forEach(s -> {
            var account = platformAccountApiFeign.getTenantPlatformAccount(s.getTenantId(), s.getPlatformAccount());
            var skuIds = Arrays.stream(s.getGoodsSkuIds().split(",")).collect(Collectors.toSet());

            // 获取feed状态信息，因为upload的时候是批量的，这里加一个缓存
            UploadStatusDTO uploadStatus;
            if (uploadStatusCache.containsKey(s.getFeedResultId())) {
                uploadStatus = uploadStatusCache.get(s.getFeedResultId());
            } else {
                uploadStatus = ecoGoodsFacadeApiFeign.getUploadStatus(account.getPlatform(), account.getClientId(),
                        account.getClientSecret(), s.getFeedResultId());
                uploadStatusCache.put(s.getFeedResultId(), uploadStatus);
            }

            // 处理feed状态
            switch (uploadStatus.getStatus()) {
                case "succeed":
                    s.setStatus(PublishStatusEnum.UPLOADED.getStatus());
                    // todo 保存产品id
                    break;
                case "failed":
                    s.setStatus(PublishStatusEnum.FAILED.getStatus());
                    s.setMessage(uploadStatus.getMessage());
                    break;
                case "processing":
                default:
                    return;
            }

            // 处理详情
            var feedResultList = uploadStatus.getDetails().stream()
                    .filter(detail -> skuIds.contains(detail.getSkuId()))
                    .map(detail -> {
                        FeedResult feedResult = new FeedResult();
                        BeanUtils.copyProperties(detail, feedResult);
                        return feedResult;
                    }).collect(Collectors.toList());
            s.setFeedResultDetail(feedResultList);

            // 更新信息
            feedStatusDaoService.updateById(s);
        });
    }

    @Override
    protected void doEnableSale(List<FeedStatusDO> feedStatusList) {
        // 商品上架，更新状态
        feedStatusList.stream().filter(s -> !StringUtils.isEmpty(s.getTargetGoodsId())).forEach(s -> {
            var account = platformAccountApiFeign.getTenantPlatformAccount(s.getTenantId(), s.getPlatformAccount());
            ecoGoodsFacadeApiFeign.enableProductSale(account.getPlatform(), account.getClientId(), account.getClientSecret(),
                    Collections.singletonList(s.getTargetGoodsId()));
            log.info("Goods {} in sale", s.getGoodsId());
            s.setStatus(PublishStatusEnum.ON_SALE.getStatus());
            feedStatusDaoService.updateById(s);
        });
    }

    @Override
    protected void doDisableSale(List<FeedStatusDO> feedStatusList) {
        // 商品上架，更新状态
        feedStatusList.stream().filter(s -> !StringUtils.isEmpty(s.getTargetGoodsId())).forEach(s -> {
            var account = platformAccountApiFeign.getTenantPlatformAccount(s.getTenantId(), s.getPlatformAccount());
            ecoGoodsFacadeApiFeign.disableProductSale(account.getPlatform(), account.getClientId(), account.getClientSecret(),
                    Collections.singletonList(s.getTargetGoodsId()));
            log.info("Goods {} disalbe sale", s.getGoodsId());
            s.setStatus(PublishStatusEnum.DISABLE_SALE.getStatus());
            feedStatusDaoService.updateById(s);
        });
    }
}
