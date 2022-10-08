package pers.kun.publish.core.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : qihang.liu
 * @date 2021-10-01
 */
@Component
public class FeedLifeCycleManager {
    @Autowired
    private FeedLifeCycle feedLifeCycle;

    public void uploadGoods(FeedContext feedContext) {
        feedLifeCycle.upload(feedContext);
    }

    public void syncUploadStatus(FeedContext feedContext) {
        feedLifeCycle.syncUploadStatus(feedContext);
    }

    public void enableSale(FeedContext feedContext) {
        feedLifeCycle.enableSale(feedContext);
    }

    public void disableSale(FeedContext feedContext) {
        feedLifeCycle.disableSale(feedContext);
    }
}
