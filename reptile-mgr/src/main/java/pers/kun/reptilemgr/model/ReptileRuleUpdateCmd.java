package pers.kun.reptilemgr.model;

import lombok.Data;
import pers.kun.core.convert.BeanConverter;
import pers.kun.core.web.model.BaseUpdateCmd;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class ReptileRuleUpdateCmd extends BaseUpdateCmd<ReptileRuleDO> {
    /**
     * id
     */
    @NotNull
    private Long id;

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

    public static BeanConverter<ReptileRuleUpdateCmd, ReptileRuleDO> convertReptileConfigEntity() {
        return BeanConverter.of(ReptileRuleUpdateCmd.class, ReptileRuleDO.class);
    }
}
