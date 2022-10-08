package pers.kun.publish.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;
import pers.kun.internal.client.product.ProductDTO;

/**
 * 刊登记录
 */
@Data
@TableName(value = "t_publish_item", autoResultMap = true)
public class PublishItemDO extends BaseTenantDO {
    /**
     * publish config id
     */
    private Long publishConfigId;

    /**
     * 刊登的产品id(t_goods)
     */
    private Long goodsId;

    /**
     * 刊登数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ProductDTO publishData;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;
}
