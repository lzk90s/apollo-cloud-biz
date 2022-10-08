package pers.kun.reptilemgr.model;

import lombok.Data;
import pers.kun.core.convert.BeanConverter;
import pers.kun.core.web.model.BaseAddCmd;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class ReptileRuleAddCmd extends BaseAddCmd<ReptileRuleDO> {
    /**
     * 名字
     */
    @NotBlank
    private String name;

    /**
     * 平台
     */
    @NotBlank
    private String spiderName;

    /**
     * 执行表达式
     */
    @NotBlank
    private String execExpr;

    /**
     * 选项
     */
    @NotBlank
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

    public static BeanConverter<ReptileRuleAddCmd, ReptileRuleDO> convertReptileConfigEntity() {
        return BeanConverter.of(ReptileRuleAddCmd.class, ReptileRuleDO.class);
    }
}
