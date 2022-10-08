package pers.kun.reptilemgr.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
@TableName(value = "t_reptile_rule", autoResultMap = true)
public class ReptileRuleDO extends BaseTenantDO {
    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 名字
     */
    private String name;

    /**
     * 爬虫名字
     */
    private String spiderName;

    /**
     * 执行表达式
     */
    private String execExpr;

    /**
     * 平台labels
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> options;

    /**
     * 标签
     */
    private String labels;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否被删除
     */
    private Boolean deleted;
}
