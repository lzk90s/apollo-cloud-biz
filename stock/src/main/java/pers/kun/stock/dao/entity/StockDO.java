package pers.kun.stock.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.base.BaseDO;

import java.util.Date;

/**
 * 商品对象
 */
@Data
@TableName(value = "t_stock")
public class StockDO extends BaseDO {
    private String code;

    private String type;

    private String name;

    private Date ipoDate;

    private Date outDate;

    private String industry;

    private String industryClassification;

    private Boolean status;
}
