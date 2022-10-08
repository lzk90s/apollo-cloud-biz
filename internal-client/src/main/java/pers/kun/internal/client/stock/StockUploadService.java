package pers.kun.internal.client.stock;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2022-01-10
 */
@RequestMapping("/internal")
public interface StockUploadService {
    @PostMapping("/stock_basic")
    void batchUploadStockBasic(@RequestBody @NotEmpty List<StockDTO> dtoList);

    @PostMapping("/stock_daily")
    void batchUploadStockDaily(@RequestBody @NotEmpty List<StockDailyDTO> dtoList);

    @PostMapping("/stock_hq")
    void batchUploadStockHQ(@RequestBody @NotEmpty List<StockHQDTO> dtoList);
}
