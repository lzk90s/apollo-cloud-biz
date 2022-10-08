package pers.kun.operation.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.operation.dao.entity.FollowSellRuleDO;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class FollowSellRulePageQuery extends BasePageQuery<FollowSellRuleDO> {
    private String platform;
    private String goodsId;
    private String skuId;

    @Override
    public QueryWrapper<FollowSellRuleDO> buildQueryWrapper() {
        return new QueryWrapper<FollowSellRuleDO>()
                .eq(StringUtils.isNotBlank(platform), "platform", platform)
                .eq(StringUtils.isNotBlank(goodsId), "goods_id", goodsId)
                .eq(StringUtils.isNotBlank(skuId), "sku_id", skuId);
    }
}
