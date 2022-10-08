package pers.kun.erp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pers.kun.core.auth.UserConst;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.erp.dao.entity.HandleUserDO;
import pers.kun.erp.dao.mapper.HandleUserMapper;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Service
public class HandleUserDaoService extends DaoServiceImpl<HandleUserMapper, HandleUserDO> {
    public boolean exist(String tenantId, String name) {
        var wrapper = new QueryWrapper<HandleUserDO>()
                .eq("name", name)
                .eq(UserConst.TENANT_ID, tenantId);
        return this.getOne(wrapper) != null;
    }
}
