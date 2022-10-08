package pers.kun.operation.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.auth.UserProvider;
import pers.kun.operation.dao.entity.FollowSellRuleDO;
import pers.kun.operation.service.FollowSellCheckService;
import pers.kun.operation.service.FollowSellRuleDaoService;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@RestController
@RequestMapping("/tasks/follow_sell_price_check_task")
public class FollowSellPriceCheckTask {
    @Autowired
    private FollowSellRuleDaoService followSellRuleDaoService;
    @Autowired
    private FollowSellCheckService followSellCheckService;

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 10 * 1000)
    @GetMapping("/exec")
    public void exec() {
        var tenants = followSellRuleDaoService.getAllTenant();
        if (CollectionUtils.isEmpty(tenants)) {
            return;
        }

        tenants.forEach(s -> {
            UserProvider.setLocalTenant(s);

            var list = followSellRuleDaoService.list(new QueryWrapper<FollowSellRuleDO>()
                    .ne("status", 0)
                    .eq("deleted", false));
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            followSellCheckService.batchCheckPrice(list);
        });
    }
}
