package pers.kun.publish.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.publish.core.PublishRuleLoader;
import pers.kun.publish.core.feed.FeedContextBuilder;
import pers.kun.publish.core.feed.FeedLifeCycleManager;

@Slf4j
@Component
@RestController
@RequestMapping("/tasks/feed_lifecycle_task")
public class FeedLifeCycleTask {
    @Autowired
    private PublishRuleLoader publishRuleLoader;
    @Autowired
    private FeedLifeCycleManager feedLifeCycleManager;

    @GetMapping("/upload")
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void upload() {
        log.info("Start upload task");
        var configs = publishRuleLoader.loadPublishConfig();
        FeedContextBuilder.buildFromPublishConfig(configs).forEach(s -> feedLifeCycleManager.uploadGoods(s));
    }

    @GetMapping("/sync_upload_status")
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void syncUploadStatus() {
        log.info("Start sync upload status task");
        var configs = publishRuleLoader.loadPublishConfig();
        FeedContextBuilder.buildFromPublishConfig(configs).forEach(s -> feedLifeCycleManager.syncUploadStatus(s));
    }

    @GetMapping("/enable_sale")
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void enableSale() {
        log.info("Start enable sale task");
        var configs = publishRuleLoader.loadPublishConfig();
        FeedContextBuilder.buildFromPublishConfig(configs).forEach(s -> feedLifeCycleManager.enableSale(s));
    }

    @GetMapping("/disable_sale")
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void disableSale() {
        log.info("Start disable sale task");
        var configs = publishRuleLoader.loadPublishConfig();
        FeedContextBuilder.buildFromPublishConfig(configs).forEach(s -> feedLifeCycleManager.disableSale(s));
    }
}
