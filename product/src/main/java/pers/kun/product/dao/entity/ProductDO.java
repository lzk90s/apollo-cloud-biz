package pers.kun.product.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import pers.kun.core.base.BaseDO;

import java.util.List;
import java.util.Map;

/**
 * 商品对象
 */
@Data
@TableName(value = "t_product", autoResultMap = true)
public class ProductDO extends BaseDO {
    /**
     * 采集规则id
     */
    private Long reptileRuleId;

    /**
     * 商品所属平台
     */
    private String platform;

    /**
     * 语言
     */
    private String language;

    /**
     * 类别
     */
    private String category;

    /**
     * 主题
     */
    private String subject;

    /**
     * 描述
     */
    private String description;

    /**
     * 品牌
     */
    private String brand;

    /**
     * url
     */
    private String detailUrl;

    /**
     * 主图url
     */
    private String mainImageUrl;

    /**
     * 商品其他图片
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> extraImageUrls;

    /**
     * 商品特征
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> feature;
}
