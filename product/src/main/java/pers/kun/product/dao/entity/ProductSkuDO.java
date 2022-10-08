package pers.kun.product.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import pers.kun.core.base.BaseDO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品sku对象
 */
@Data
@TableName(value = "t_product_sku", autoResultMap = true)
public class ProductSkuDO extends BaseDO {

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 采集规则id
     */
    private Long reptileRuleId;

    /**
     * sku名字
     */
    private String name;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 商品属性（size，color）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> skuFeature;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 价格单位
     */
    private String priceUnit;

    /**
     * 运费
     */
    private BigDecimal shippingFee;

    /**
     * 数量
     */
    private Integer storage;

    /**
     * 重量
     */
    private String weight;

    /**
     * 跟卖信息，json list
     */
    private String followSells;
}
