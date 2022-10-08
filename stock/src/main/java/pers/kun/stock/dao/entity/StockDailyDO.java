package pers.kun.stock.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.base.BaseDO;

import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2022-01-05
 */
@Data
@TableName("t_stock_daily")
public class StockDailyDO extends BaseDO {
    private String code;

    private Date date;

    private Float open;

    private Float high;

    private Float low;

    @TableField("`close`")
    private Float close;

    private Float preClose;

    @TableField("`change`")
    private Float change;

    private Float changePercent;

    private Long volume;

    private Long amount;

    private Float turn;

    private Float peTtm;

    /**
     * 总市值
     */
    private Double tcap;

    /**
     * 流通市值
     */
    private Double mcap;
}
