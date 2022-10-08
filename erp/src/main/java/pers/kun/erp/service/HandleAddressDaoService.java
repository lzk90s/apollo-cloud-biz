package pers.kun.erp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pers.kun.core.auth.UserConst;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.erp.dao.entity.HandleAddressDO;
import pers.kun.erp.dao.mapper.HandleAddressMapper;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Service
public class HandleAddressDaoService extends DaoServiceImpl<HandleAddressMapper, HandleAddressDO> {
    public boolean exist(String tenantId, String name) {
        var wrapper = new QueryWrapper<HandleAddressDO>()
                .eq("name", name)
                .eq(UserConst.TENANT_ID, tenantId);
        return this.getOne(wrapper) != null;
    }
}
