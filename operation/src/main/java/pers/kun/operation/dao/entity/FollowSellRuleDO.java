package pers.kun.operation.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

/**
 * 跟卖规则
 */
@Data
@TableName(value = "t_follow_sell_rule", autoResultMap = true)
public class FollowSellRuleDO extends BaseTenantDO {

    /**
     * 平台
     */
    private String platform;

    /**
     * 商品id
     */
    private Long goodsId;

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
