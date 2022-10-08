package pers.kun.reptilemgr.service;

import org.springframework.stereotype.Service;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;
import pers.kun.reptilemgr.dao.mapper.ReptileRuleMapper;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Service
public class ReptileRuleDaoService extends DaoServiceImpl<ReptileRuleMapper, ReptileRuleDO> {
    public List<String> getAllTenant() {
        return getBaseMapper().getAllTenant("t_reptile_rule");
    }
}
