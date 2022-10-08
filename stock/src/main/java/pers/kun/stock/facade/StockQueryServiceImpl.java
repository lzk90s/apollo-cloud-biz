package pers.kun.stock.facade;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.internal.client.stock.StockQueryService;
import pers.kun.stock.dao.entity.StockDO;
import pers.kun.stock.service.StockDaoService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2022-01-10
 */
@RestController
public class StockQueryServiceImpl implements StockQueryService {
    @Autowired
    private StockDaoService stockDaoService;

    @Override
    public List<String> getAllStockCodes(@RequestParam(required = false) Float maxTcap) {
        var result = stockDaoService.list(new QueryWrapper<StockDO>().eq("status", 1));
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }
        return result.stream()
                .map(StockDO::getCode)
                .filter(s -> !s.startsWith("sz.300")).collect(Collectors.toList());
    }
}
