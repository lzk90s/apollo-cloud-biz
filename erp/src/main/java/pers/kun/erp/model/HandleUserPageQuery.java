package pers.kun.erp.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.erp.dao.entity.HandleUserDO;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
public class HandleUserPageQuery extends BasePageQuery<HandleUserDO> {
    private String name;

    @Override
    public QueryWrapper<HandleUserDO> buildQueryWrapper() {
        return new QueryWrapper<HandleUserDO>()
                .eq(StringUtils.isNotBlank(name), "name", name);
    }
}
