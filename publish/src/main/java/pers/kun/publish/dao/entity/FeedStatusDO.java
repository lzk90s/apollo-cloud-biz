package pers.kun.publish.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

import java.util.List;

/**
 * 刊登记录
 */
@Data
@TableName(value = "t_feed_status", autoResultMap = true)
public class FeedStatusDO extends BaseTenantDO {
    /**
     * publish config id
     */
    private Long publishConfigId;

    /**
     * publish item id
     */
    private Long publishItemId;

    /**
     * 平台
     */
    private String platform;

    /**
     * 目标平台账号
     */
    private String platformAccount;

    /**
     * 刊登的产品id(t_goods)
     */
    private Long goodsId;

    /**
     * 刊登的产品id列表(t_goods_sku)
     */
    private String goodsSkuIds;

    /**
     * 目标平台上的产品id
     */
    private String targetGoodsId;

    /**
     * feed 结果 id
     */
    private String feedResultId;

    /**
     * feed 结果详情
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<FeedResult> feedResultDetail;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String message;
}
