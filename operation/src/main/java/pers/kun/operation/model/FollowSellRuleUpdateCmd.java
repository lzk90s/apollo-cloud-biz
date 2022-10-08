package pers.kun.operation.model;

import lombok.Data;
import pers.kun.core.web.model.BaseUpdateCmd;
import pers.kun.operation.dao.entity.FollowSellRuleDO;

import javax.validation.constraints.NotNull;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class FollowSellRuleUpdateCmd extends BaseUpdateCmd<FollowSellRuleDO> {
    /**
     * id
     */
    @NotNull
    Long id;

    /**
     * 价格步长
     */
    @NotNull
    private Float priceStep;

    /**
     * 最小价格
     */
    @NotNull
    private Float minPrice;
}
