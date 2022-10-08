package pers.kun.operation.model;

import lombok.Data;
import pers.kun.core.base.BaseVO;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class FollowSellRuleVO extends BaseVO {


    /**
     * 平台
     */
    private String platform;

    /**
     * 商品id
     */
    private String goodsId;

    /**
     * sku id
     */
    private String skuId;

    /**
     * 价格步长
     */
    private Float priceStep;

    /**
     * 最小价格
     */
    private Float minPrice;

    private Integer status;
}
