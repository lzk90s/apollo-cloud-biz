package pers.kun.internal.client.vendor_integration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 货币api
 *
 * @author : qihang.liu
 * @date 2021-05-16
 */
@RequestMapping("/currency")
public interface CurrencyFacadeApi {
    @GetMapping("/exchange_rate")
    Float getExchangeRate(@RequestParam("from") String from, @RequestParam("to") String to);
}
