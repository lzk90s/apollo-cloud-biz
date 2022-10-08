package pers.kun.internal.client.reptile_control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@RequestMapping("/internal/reptile_rule")
public interface ReptileRuleMService {
    /**
     * spider使用
     *
     * @param spider
     * @return
     */
    @GetMapping("/by_spider")
    List<ReptileRuleDTO> getRuleBySpiderAllTenant(@RequestParam("spider") String spider);

    @GetMapping("/by_labels")
    List<ReptileRuleDTO> getRuleByLabelsAllTenant(@RequestParam("labels") String labels);
}
