package pers.kun.stock.service;

import org.springframework.stereotype.Service;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.stock.dao.entity.StockDailyDO;
import pers.kun.stock.dao.mapper.StockDailyMapper;

/**
 * @author : qihang.liu
 * @date 2022-01-08
 */
@Service
public class StockDailyDaoService extends DaoServiceImpl<StockDailyMapper, StockDailyDO> {
}
