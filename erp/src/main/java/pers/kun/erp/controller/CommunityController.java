package pers.kun.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.erp.dao.entity.CommunityDO;
import pers.kun.erp.dao.mapper.CommunityMapper;
import pers.kun.erp.model.CommunityAddCmd;
import pers.kun.erp.model.CommunityPageQuery;
import pers.kun.erp.model.CommunityUpdateCmd;
import pers.kun.erp.model.CommunityVO;
import pers.kun.erp.service.CommunityDaoService;

/**
 * @author : qihang.liu
 * @date 2022-08-15
 */
@RestController
@RequestMapping("/community")
public class CommunityController implements BaseCurdController<
        CommunityMapper,
        CommunityDO,
        CommunityVO,
        CommunityAddCmd,
        CommunityUpdateCmd,
        CommunityPageQuery> {

    @Autowired
    private CommunityDaoService daoService;

    @Override
    public DaoServiceImpl<CommunityMapper, CommunityDO> getDaoService() {
        return daoService;
    }

    @Override
    public Class<CommunityVO> getVoClass() {
        return CommunityVO.class;
    }

    @Override
    public Class<CommunityDO> getDoClass() {
        return CommunityDO.class;
    }
}
