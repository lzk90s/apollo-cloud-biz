package pers.kun.messager.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.util.JsonUtil;
import pers.kun.internal.client.messager.MessageMService;
import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.messager.core.MessageRouter;
import pers.kun.messager.core.MessageSenderSelector;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/internal/message")
public class MessageMServiceImpl implements MessageMService {
    @Autowired
    private MessageRouter messageRouter;

    @Override
    public void sendMessage(@RequestBody MessageDTO message) {
        log.info("Received message {}", JsonUtil.obj2json(message));
        var route = messageRouter.getMessageRoute(message.getTopic());
        Optional.ofNullable(route).orElseThrow(UnsupportedOperationException::new);
        MessageSenderSelector.select(route.getType()).send(route, message);
    }
}
