package pers.kun.reptilemgr.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author : qihang.liu
 * @date 2021-09-17
 */
@Slf4j
@RestController
@RequestMapping("/internal/reptile/history")
public class ReptileHistoryApiImpl {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${reptile.history.ttl:3600}")
    private int defaultTtl;

    private static final String KEY_PREFIX = "reptile_history:";

    @PostMapping
    public void saveHistory(@RequestParam("key") String key,
                            @RequestParam(value = "ttl", required = false) Integer ttl) {
        ttl = Optional.ofNullable(ttl).orElse(defaultTtl);
        redisTemplate.opsForValue().set(buildKey(key), "ok", ttl, TimeUnit.SECONDS);
        log.info("Save reptile history, key {}", key);
    }

    @GetMapping("/")
    public boolean isHistoryExist(@RequestParam("key") String key) {
        return redisTemplate.opsForValue().get(buildKey(key)) != null;
    }

    private String buildKey(String key) {
        return KEY_PREFIX + key;
    }
}
