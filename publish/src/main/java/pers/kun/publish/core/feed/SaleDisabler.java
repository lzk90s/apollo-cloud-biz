package pers.kun.publish.core.feed;

/**
 * 下架
 *
 * @author : qihang.liu
 * @date 2021-05-23
 */
public interface SaleDisabler {
    void disableSale(FeedContext feedContext);
}
