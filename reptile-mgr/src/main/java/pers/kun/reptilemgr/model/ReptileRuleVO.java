package pers.kun.reptilemgr.model;

import lombok.Data;
import pers.kun.core.base.BaseVO;
import pers.kun.core.convert.BeanConverter;
import pers.kun.internal.client.reptile_control.ReptileRuleDTO;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;

import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class ReptileRuleVO extends BaseVO {

    /**
     * 名字
     */
    private String name;

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

    public static BeanConverter<ReptileRuleDO, ReptileRuleDTO> convertReptileConfigEntity() {
        return BeanConverter.of(ReptileRuleDO.class, ReptileRuleDTO.class);
    }
}
