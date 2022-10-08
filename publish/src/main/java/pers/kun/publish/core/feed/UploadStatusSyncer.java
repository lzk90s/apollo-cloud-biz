package pers.kun.publish.core.feed;

/**
 * 商品状态同步
 *
 * @author : qihang.liu
 * @date 2021-05-23
 */
public interface UploadStatusSyncer {
    void syncUploadStatus(FeedContext feedContext);
}
