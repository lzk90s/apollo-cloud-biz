package pers.kun.stock.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.convert.BeanConverter;
import pers.kun.internal.client.stock.StockDTO;
import pers.kun.internal.client.stock.StockDailyDTO;
import pers.kun.internal.client.stock.StockHQDTO;
import pers.kun.internal.client.stock.StockUploadService;
import pers.kun.stock.dao.entity.StockDO;
import pers.kun.stock.dao.entity.StockDailyDO;
import pers.kun.stock.dao.entity.StockHQDO;
import pers.kun.stock.service.StockDailyDaoService;
import pers.kun.stock.service.StockDaoService;
import pers.kun.stock.service.StockHQDaoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StockUploadServiceImpl implements StockUploadService {
    @Autowired
    private StockHQDaoService stockHQDaoService;
    @Autowired
    private StockDailyDaoService stockDailyDaoService;
    @Autowired
    private StockDaoService stockDaoService;

    @Override
    public void batchUploadStockBasic(List<StockDTO> dtoList) {
        var list = dtoList.stream()
                .map(s -> BeanConverter.of(StockDTO.class, StockDO.class).s2t(s))
                .collect(Collectors.toList());
        stockDaoService.saveOrUpdateBatch(list);
    }

    @Override
    public void batchUploadStockDaily(List<StockDailyDTO> dtoList) {
        var list = dtoList.stream()
                .map(s -> BeanConverter.of(StockDailyDTO.class, StockDailyDO.class).s2t(s))
                .collect(Collectors.toList());
        stockDailyDaoService.saveOrUpdateBatch(list);
    }

    @Override
    public void batchUploadStockHQ(List<StockHQDTO> dtoList) {
        var list = dtoList.stream()
                .map(s -> BeanConverter.of(StockHQDTO.class, StockHQDO.class).s2t(s))
                .collect(Collectors.toList());
        stockHQDaoService.saveOrUpdateBatch(list);
    }
}
