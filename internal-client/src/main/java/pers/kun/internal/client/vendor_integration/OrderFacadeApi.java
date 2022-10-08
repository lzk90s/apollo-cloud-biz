package pers.kun.internal.client.vendor_integration;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/order")
public interface OrderFacadeApi {
    @GetMapping("/list_unhandled_order/{platform}")
    List<OrderDTO> listUnhandledOrder(@PathVariable("platform") String platform,
                                      @RequestParam("clientId") String clientId,
                                      @RequestParam("clientSecret") String clientSecret,
                                      @RequestParam("cookies") String cookies);

    @GetMapping("/list_refund_order/{platform}")
    List<RefundOrderDTO> listRefundOrder(@PathVariable("platform") String platform,
                                         @RequestParam("clientId") String clientId,
                                         @RequestParam("clientSecret") String clientSecret,
                                         @RequestParam("cookies") String cookies);
}
