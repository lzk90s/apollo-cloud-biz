//package pers.kun.messager.core.support;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import pers.kun.internal.client.messager.MessageDTO;
//import pers.kun.messager.core.AbstractMessageSender;
//import pers.kun.messager.enums.MessageTypeEnum;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @author : qihang.liu
// * @date 2021-12-05
// */
//@Slf4j
//@Service("email")
//public class EmailMessageSender extends AbstractMessageSender {
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String from;
//
//    @Override
//    protected List<MessageTypeEnum> getMessageType() {
//        return Arrays.asList(MessageTypeEnum.TEXT);
//    }
//
//    @Override
//    public void send(String destination, String secret, MessageTypeEnum messageType, MessageDTO message) {
//        try {
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            helper.setFrom(from);
//            helper.setTo(destination);
//            helper.setSubject(message.getTitle());
//            helper.setText(message.getMessage(), true);
//            mailSender.send(mimeMessage);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
