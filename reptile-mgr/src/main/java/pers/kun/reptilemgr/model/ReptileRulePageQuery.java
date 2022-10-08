package pers.kun.reptilemgr.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class ReptileRulePageQuery extends BasePageQuery<ReptileRuleDO> {
    private String name;
    private String spiderName;

    @Override
    public QueryWrapper<ReptileRuleDO> buildQueryWrapper() {
        return new QueryWrapper<ReptileRuleDO>()
                .eq(StringUtils.isNotBlank(name), "name", name)
                .eq(StringUtils.isNotBlank(spiderName), "spider_name", spiderName);
    }
}
