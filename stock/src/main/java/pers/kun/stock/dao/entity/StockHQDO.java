package pers.kun.stock.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.base.BaseDO;

import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2022-01-06
 */
@Data
@TableName("t_stock_hq")
public class StockHQDO extends BaseDO {
    private String code;

    private Date hqTime;

    private Float price;

    @TableField("`change`")
    private Float change;

    private Float changePercent;
}
