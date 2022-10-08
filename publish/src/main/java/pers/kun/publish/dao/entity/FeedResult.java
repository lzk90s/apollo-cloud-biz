package pers.kun.publish.dao.entity;

import lombok.Data;

/**
 * @author : qihang.liu
 * @date 2021-10-07
 */
@Data
public class FeedResult {
    /**
     * sku id
     */
    private String skuId;
    /**
     * 是否成功
     */
    private Boolean succeed;
    /**
     * 异常消息
     */
    private String message;
}
