package pers.kun.internal.client.stock;

import org.springframework.web.bind.annotation.RequestMapping;
import pers.kun.core.facade.MService;

/**
 * @author : qihang.liu
 * @date 2022-01-19
 */
@RequestMapping("/internal/stock")
public interface StockMService extends MService<StockDTO> {
}
