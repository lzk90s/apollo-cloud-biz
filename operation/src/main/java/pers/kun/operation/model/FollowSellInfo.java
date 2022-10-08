package pers.kun.operation.model;

import lombok.Data;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Data
public class FollowSellInfo {
    /**
     * 卖家
     */
    private String seller;
    /**
     * 价格
     */
    private Float price;
}
