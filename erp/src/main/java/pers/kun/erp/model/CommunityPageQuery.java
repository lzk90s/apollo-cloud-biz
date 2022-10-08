package pers.kun.erp.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.erp.dao.entity.CommunityDO;

/**
 * @author : qihang.liu
 * @date 2022-08-15
 */
@Data
public class CommunityPageQuery extends BasePageQuery<CommunityDO> {
    private String code;

    @Override
    public QueryWrapper<CommunityDO> buildQueryWrapper() {
        QueryWrapper<CommunityDO> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(code), "code", code);
        return wrapper;
    }
}
