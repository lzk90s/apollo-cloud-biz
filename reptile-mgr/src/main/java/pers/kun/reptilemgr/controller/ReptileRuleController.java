package pers.kun.reptilemgr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;
import pers.kun.reptilemgr.dao.mapper.ReptileRuleMapper;
import pers.kun.reptilemgr.model.ReptileRuleAddCmd;
import pers.kun.reptilemgr.model.ReptileRulePageQuery;
import pers.kun.reptilemgr.model.ReptileRuleUpdateCmd;
import pers.kun.reptilemgr.model.ReptileRuleVO;
import pers.kun.reptilemgr.service.ReptileRuleDaoService;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@RestController
@RequestMapping("/reptile_rule")
public class ReptileRuleController implements BaseCurdController<
        ReptileRuleMapper,
        ReptileRuleDO,
        ReptileRuleVO,
        ReptileRuleAddCmd,
        ReptileRuleUpdateCmd,
        ReptileRulePageQuery
        > {

    @Autowired
    private ReptileRuleDaoService reptileRuleDaoService;

    @Override
    public DaoServiceImpl<ReptileRuleMapper, ReptileRuleDO> getDaoService() {
        return reptileRuleDaoService;
    }

    @Override
    public Class<ReptileRuleVO> getVoClass() {
        return ReptileRuleVO.class;
    }

    @Override
    public Class<ReptileRuleDO> getDoClass() {
        return ReptileRuleDO.class;
    }
}
