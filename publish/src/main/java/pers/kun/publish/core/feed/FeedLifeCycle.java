package pers.kun.publish.core.feed;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import pers.kun.publish.core.PublishStatusEnum;
import pers.kun.publish.dao.entity.FeedStatusDO;
import pers.kun.publish.integration.EcoGoodsFacadeApiFeign;
import pers.kun.publish.integration.PlatformAccountMServiceFeign;
import pers.kun.publish.service.FeedStatusDaoService;

import java.util.Collections;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
@Slf4j
public abstract class FeedLifeCycle implements Uploader, UploadStatusSyncer, SaleEnabler, SaleDisabler {
    @Autowired
    protected FeedStatusDaoService feedStatusDaoService;
    @Autowired
    protected EcoGoodsFacadeApiFeign ecoGoodsFacadeApiFeign;
    @Autowired
    protected PlatformAccountMServiceFeign platformAccountApiFeign;
    @Value("${feed.batchNum:20}")
    private int batchNum;

    /**
     * 上传
     *
     * @param feedStatusList
     */
    protected abstract void doUpload(List<FeedStatusDO> feedStatusList);

    /**
     * 同步上传状态
     *
     * @param feedStatusList
     */
    protected abstract void doSyncUploadStatus(List<FeedStatusDO> feedStatusList);

    /**
     * 上架
     *
     * @param feedStatusList
     */
    protected abstract void doEnableSale(List<FeedStatusDO> feedStatusList);

    /**
     * 下架
     *
     * @param feedStatusList
     */
    protected abstract void doDisableSale(List<FeedStatusDO> feedStatusList);

    @Override
    public void upload(FeedContext feedContext) {
        doUpload(getFeedListByStatus(feedContext, PublishStatusEnum.CONFIRMED));
    }

    @Override
    public void syncUploadStatus(FeedContext feedContext) {
        doSyncUploadStatus(getFeedListByStatus(feedContext, PublishStatusEnum.UPLOADING));
    }

    @Override
    public void enableSale(FeedContext feedContext) {
        doEnableSale(getFeedListByStatus(feedContext, PublishStatusEnum.UPLOADED));
    }

    @Override
    public void disableSale(FeedContext feedContext) {
        doDisableSale(getFeedListByStatus(feedContext, PublishStatusEnum.WAIT_DISABLE_SALE));
    }

    private List<FeedStatusDO> getFeedListByStatus(FeedContext feedContext, PublishStatusEnum status) {
        var recordList = feedStatusDaoService
                .page(new Page<>(0, batchNum), new QueryWrapper<FeedStatusDO>()
                        .eq("tenant_id", feedContext.getTenantId())
                        .eq("publish_config_id", feedContext.getPublishConfigId())
                        .eq("platform", feedContext.getDstPlatform())
                        .eq("platform_account", feedContext.getDstPlatformAccount())
                        .eq("status", status.getStatus()));
        if (CollectionUtils.isEmpty(recordList.getRecords())) {
            return Collections.emptyList();
        }
        return recordList.getRecords();
    }
}
