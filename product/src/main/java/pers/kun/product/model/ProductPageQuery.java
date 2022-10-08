package pers.kun.product.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.product.dao.entity.ProductDO;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class ProductPageQuery extends BasePageQuery<ProductDO> {
    private String category;
    private String platform;
    private Long reptileRuleId;

    @Override
    public QueryWrapper<ProductDO> buildQueryWrapper() {
        return new QueryWrapper<ProductDO>()
                .eq(StringUtils.isNotBlank(category), "category", category)
                .eq(StringUtils.isNotBlank(platform), "platform", platform)
                .eq(null != reptileRuleId, "reptile_rule_id", reptileRuleId)
                .orderByDesc("add_time");
    }
}
