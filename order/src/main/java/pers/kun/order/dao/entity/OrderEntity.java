package pers.kun.order.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

import java.util.Date;

@Data
@TableName("t_order")
public class OrderEntity extends BaseTenantDO {
    /**
     * 平台
     */
    private String platform;

    /**
     * 平台账号
     */
    private String platformAccount;

    /**
     * 类型
     */
    private String type;

    /**
     * 确认时间
     */
    private Date confirmTime;

    /**
     * sn
     */
    private String sn;

    /**
     * sku
     */
    private String sku;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 价格
     */
    private Float price;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 详细url
     */
    private String detailUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 最后通知时间
     */
    private Date lastNotifyTime;
}
