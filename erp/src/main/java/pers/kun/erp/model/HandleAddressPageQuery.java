package pers.kun.erp.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.erp.dao.entity.HandleAddressDO;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
public class HandleAddressPageQuery extends BasePageQuery<HandleAddressDO> {
    private String name;

    @Override
    public QueryWrapper<HandleAddressDO> buildQueryWrapper() {
        return new QueryWrapper<HandleAddressDO>()
                .eq(StringUtils.isNotBlank(name), "name", name);
    }
}
