package pers.kun.internal.client.reptile_control;

import lombok.Data;

import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Data
public class ReptileRuleDTO {
    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 平台
     */
    private String spiderName;

    /**
     * 执行表达式
     */
    private String execExpr;

    /**
     * 选项
     */
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

    /**
     * 关联商品数量
     */
    private Long relatedGoodsNum;
}
