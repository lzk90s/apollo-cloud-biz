package pers.kun.internal.client.vendor_integration;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 翻译api
 *
 * @author : qihang.liu
 * @date 2021-04-29
 */
@RequestMapping("/translator")
public interface TranslateFacadeApi {
    @PostMapping("/translate")
    String translate(@RequestParam("from_lang") String fromLang, @RequestParam("to_lang") String toLang, @RequestBody TextDTO text);
}
