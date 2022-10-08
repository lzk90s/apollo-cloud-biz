package pers.kun.internal.client.vendor_integration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-09-29
 */

public interface FenciFacadeApi {
    @GetMapping("/fenci/cut")
    List<String> cutWords(@RequestParam("content") String content);
}
