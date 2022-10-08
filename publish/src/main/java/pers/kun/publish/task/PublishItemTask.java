package pers.kun.publish.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.publish.core.PublishRuleLoader;
import pers.kun.publish.core.item.PublishItemManager;

/**
 * @author : qihang.liu
 * @date 2021-10-06
 */
@Slf4j
@Component
@RestController
@RequestMapping("/tasks/publish_item_task")
public class PublishItemTask {
    @Autowired
    private PublishItemManager publishItemManager;
    @Autowired
    private PublishRuleLoader publishRuleLoader;

    @GetMapping("/generate")
    @Scheduled(fixedDelay = 60 * 1000)
    public void generate() {
        var configs = publishRuleLoader.loadPublishConfig();
        configs.forEach(s -> publishItemManager.generate(s, (cursor) -> publishRuleLoader.commitCursor(s.getId(), cursor)));
    }
}
