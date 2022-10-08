package pers.kun.product.model;

import lombok.Data;
import pers.kun.core.convert.BeanConverter;
import pers.kun.product.dao.entity.ProductSkuDO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-09-06
 */
@Data
public class SkuVO {
    /**
     * id
     */
    private String id;

    /**
     * 商品id
     */
    private String productId;

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
     * 商品属性
     */
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
    private Integer count;

    public static BeanConverter<ProductSkuDO, SkuVO> convertGoodsSkuEntity() {
        return BeanConverter.of(ProductSkuDO.class, SkuVO.class);
    }
}
