package pers.kun.publish.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.rest.R;
import pers.kun.publish.service.PublishConfirmService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@RestController
@Slf4j
@RequestMapping("/publish_item")
public class PublishConfirmController {
    @Autowired
    private PublishConfirmService publishConfirmService;

    @PutMapping("/batch_ignore")
    public R<List<Long>> batchIgnore(@RequestParam String ids) {
        log.info("Ignore record {}", ids);
        var idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        publishConfirmService.ignore(idList);
        return R.ok(idList);
    }

    @PutMapping("/batch_confirm")
    public R<List<Long>> batchConfirm(@RequestParam String ids) {
        log.info("Confirm record {}", ids);
        var idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        publishConfirmService.confirm(idList);
        return R.ok(idList);
    }
}
