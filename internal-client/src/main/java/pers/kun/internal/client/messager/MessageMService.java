package pers.kun.internal.client.messager;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/internal/message")
public interface MessageMService {
    @PostMapping
    void sendMessage(@RequestBody MessageDTO message);
}
