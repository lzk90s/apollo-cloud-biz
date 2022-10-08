package pers.kun.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.erp.dao.entity.HandleAddressDO;
import pers.kun.erp.dao.mapper.HandleAddressMapper;
import pers.kun.erp.model.HandleAddressAddCmd;
import pers.kun.erp.model.HandleAddressPageQuery;
import pers.kun.erp.model.HandleAddressUpdateCmd;
import pers.kun.erp.model.HandleAddressVO;
import pers.kun.erp.service.HandleAddressDaoService;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@RestController
@RequestMapping("/handle_address")
public class HandleAddressController implements BaseCurdController<
        HandleAddressMapper,
        HandleAddressDO,
        HandleAddressVO,
        HandleAddressAddCmd,
        HandleAddressUpdateCmd,
        HandleAddressPageQuery
        > {

    @Autowired
    private HandleAddressDaoService handleAddressDaoService;

    @Override
    public DaoServiceImpl<HandleAddressMapper, HandleAddressDO> getDaoService() {
        return handleAddressDaoService;
    }

    @Override
    public Class<HandleAddressVO> getVoClass() {
        return HandleAddressVO.class;
    }

    @Override
    public Class<HandleAddressDO> getDoClass() {
        return HandleAddressDO.class;
    }
}
