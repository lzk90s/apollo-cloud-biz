package pers.kun.order.task;

import pers.kun.order.service.RefundNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : qihang.liu
 * @date 2021-05-18
 */
@Slf4j
@RestController
@RequestMapping("/tasks/refund_scan_task")
@Component
public class RefundScanTask {
    @Autowired
    private RefundNotifyService refundNotifyService;

    @GetMapping("/execute")
    @Scheduled(fixedDelay = 2 * 60 * 60 * 1000)
    public void execute() {
        refundNotifyService.scanRefund();
    }
}
