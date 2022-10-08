package pers.kun.publish.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.publish.core.PublishRuleLoader;
import pers.kun.publish.core.report.PublishResultReporter;

/**
 * @author : qihang.liu
 * @date 2021-10-06
 */
@Slf4j
@Component
@RestController
@RequestMapping("/tasks/report_task")
public class ReportTask {
    @Autowired
    private PublishRuleLoader publishRuleLoader;
    @Autowired
    private PublishResultReporter publishResultReporter;

    @GetMapping("/report")
    //@Scheduled(cron = "0 0 12,18,22 * * ?")
    //@Scheduled(fixedDelay = 60*1000)
    public void report() {
        log.info("Start report task");
        var configs = publishRuleLoader.loadPublishConfig();
        configs.forEach(s -> publishResultReporter.report(s));
    }
}
