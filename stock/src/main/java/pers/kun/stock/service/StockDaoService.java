package pers.kun.stock.service;

import org.springframework.stereotype.Service;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.stock.dao.entity.StockDO;
import pers.kun.stock.dao.mapper.StockMapper;

/**
 * @author : qihang.liu
 * @date 2022-01-10
 */
@Service
public class StockDaoService extends DaoServiceImpl<StockMapper, StockDO> {
}
