package pers.kun.permission.service;

import org.springframework.stereotype.Service;
import pers.kun.permission.dao.entity.PlatformAccountDO;
import pers.kun.permission.dao.mapper.PlatformAccountMapper;
import pers.kun.core.web.DaoServiceImpl;

@Service
public class PlatformAccountDaoService extends DaoServiceImpl<PlatformAccountMapper, PlatformAccountDO> {
}
