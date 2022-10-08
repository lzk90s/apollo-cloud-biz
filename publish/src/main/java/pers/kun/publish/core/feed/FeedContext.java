package pers.kun.publish.core.feed;

import lombok.Data;

/**
 * @author : qihang.liu
 * @date 2021-10-07
 */
@Data
public class FeedContext {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * publish config id
     */
    private Long publishConfigId;

    /**
     * 目标平台
     */
    private String dstPlatform;

    /**
     * 目标平台账号（可以有多个，多个时使用逗号隔开）
     */
    private String dstPlatformAccount;
}
