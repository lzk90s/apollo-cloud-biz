package pers.kun.operation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.model.unsupported.UnsupportedAddCmd;
import pers.kun.operation.dao.entity.FollowSellRuleDO;
import pers.kun.operation.dao.mapper.FollowSellRuleMapper;
import pers.kun.operation.model.FollowSellRulePageQuery;
import pers.kun.operation.model.FollowSellRuleUpdateCmd;
import pers.kun.operation.model.FollowSellRuleVO;
import pers.kun.operation.service.FollowSellRuleDaoService;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@RestController
@RequestMapping("/follow_sell_rule")
public class FollowSellRuleController implements BaseCurdController<
        FollowSellRuleMapper,
        FollowSellRuleDO,
        FollowSellRuleVO,
        UnsupportedAddCmd<FollowSellRuleDO>,
        FollowSellRuleUpdateCmd,
        FollowSellRulePageQuery
        > {

    @Autowired
    private FollowSellRuleDaoService followSellRuleDaoService;

    @Override
    public DaoServiceImpl<FollowSellRuleMapper, FollowSellRuleDO> getDaoService() {
        return followSellRuleDaoService;
    }

    @Override
    public Class<FollowSellRuleVO> getVoClass() {
        return FollowSellRuleVO.class;
    }

    @Override
    public Class<FollowSellRuleDO> getDoClass() {
        return FollowSellRuleDO.class;
    }
}
