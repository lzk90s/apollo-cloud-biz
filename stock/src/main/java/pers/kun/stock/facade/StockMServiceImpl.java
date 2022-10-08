package pers.kun.stock.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.facade.BaseMService;
import pers.kun.internal.client.stock.StockMService;
import pers.kun.internal.client.stock.StockDTO;
import pers.kun.stock.dao.entity.StockDO;
import pers.kun.stock.dao.mapper.StockMapper;
import pers.kun.stock.service.StockDaoService;

/**
 * @author : qihang.liu
 * @date 2022-01-19
 */
@RestController
public class StockMServiceImpl extends BaseMService<StockDO, StockMapper, StockDTO> implements StockMService {
    public StockMServiceImpl(@Autowired StockDaoService service) {
        setBaseDAO(StockDO.class, StockDTO.class, service);
    }
}
