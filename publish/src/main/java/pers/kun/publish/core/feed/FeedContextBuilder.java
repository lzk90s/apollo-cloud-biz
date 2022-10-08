package pers.kun.publish.core.feed;

import pers.kun.publish.dao.entity.PublishConfigDO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-10-07
 */
public class FeedContextBuilder {
    public static List<FeedContext> buildFromPublishConfig(List<PublishConfigDO> publishConfig) {
        // 账号拆分
        List<FeedContext> feedContextList = new ArrayList<>();
        for (var config : publishConfig) {
            var accounts = config.getDstPlatformAccount().split(",");
            for (var account : accounts) {
                FeedContext context = new FeedContext();
                context.setTenantId(config.getTenantId());
                context.setPublishConfigId(config.getId());
                context.setDstPlatform(config.getDstPlatform());
                context.setDstPlatformAccount(account);
                feedContextList.add(context);
            }
        }
        return feedContextList;
    }
}
