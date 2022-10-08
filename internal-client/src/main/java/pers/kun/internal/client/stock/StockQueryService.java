package pers.kun.internal.client.stock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2022-01-10
 */
@RequestMapping("/internal")
public interface StockQueryService {
    @GetMapping("/all_stock_codes")
    List<String> getAllStockCodes(@RequestParam(required = false) Float maxTcap);
}
