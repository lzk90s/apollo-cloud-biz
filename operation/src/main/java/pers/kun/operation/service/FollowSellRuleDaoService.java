package pers.kun.operation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.operation.dao.entity.FollowSellRuleDO;
import pers.kun.operation.dao.mapper.FollowSellRuleMapper;

import java.util.List;

@Service
@Transactional
public class FollowSellRuleDaoService extends DaoServiceImpl<FollowSellRuleMapper, FollowSellRuleDO> {
    public List<String> getAllTenant() {
        return getBaseMapper().getAllTenant("t_follow_sell_rule");
    }
}
