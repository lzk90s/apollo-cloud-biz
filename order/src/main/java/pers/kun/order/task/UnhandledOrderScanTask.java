package pers.kun.order.task;

import pers.kun.order.service.UnhandledOrderNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tasks/unhandled_order_scan_task")
@Component
public class UnhandledOrderScanTask {
    @Autowired
    private UnhandledOrderNotifyService unhandledOrderNotifyService;

    @GetMapping("/execute")
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void scan() {
        unhandledOrderNotifyService.scanOrder();
    }
}
