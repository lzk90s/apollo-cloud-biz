package pers.kun.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.erp.dao.entity.HandleUserDO;
import pers.kun.erp.dao.mapper.HandleUserMapper;
import pers.kun.erp.model.HandleUserAddCmd;
import pers.kun.erp.model.HandleUserPageQuery;
import pers.kun.erp.model.HandleUserUpdateCmd;
import pers.kun.erp.model.HandleUserVO;
import pers.kun.erp.service.HandleUserDaoService;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@RestController
@RequestMapping("/handle_user")
public class HandleUserController implements BaseCurdController<
        HandleUserMapper,
        HandleUserDO,
        HandleUserVO,
        HandleUserAddCmd,
        HandleUserUpdateCmd,
        HandleUserPageQuery
        > {

    @Autowired
    private HandleUserDaoService handleUserDaoService;

    @Override
    public DaoServiceImpl<HandleUserMapper, HandleUserDO> getDaoService() {
        return handleUserDaoService;
    }

    @Override
    public Class<HandleUserVO> getVoClass() {
        return HandleUserVO.class;
    }

    @Override
    public Class<HandleUserDO> getDoClass() {
        return HandleUserDO.class;
    }
}
