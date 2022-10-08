package pers.kun.publish.core.item.vendor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pers.kun.publish.core.item.DefaultItemGenerator;
import pers.kun.publish.core.item.PublishContext;
import pers.kun.publish.integration.FenciFacadeApiFeign;

import java.util.stream.Collectors;

import static pers.kun.publish.core.item.ItemGenerator.GENERATOR;

/**
 * @author : qihang.liu
 * @date 2021-09-28
 */
@Slf4j
@Component("walmart" + GENERATOR)
public class WalmartItemGenerator extends DefaultItemGenerator {
    @Value("${subject.maxLength:200}")
    private int subjectMaxLen;

    @Autowired
    private FenciFacadeApiFeign fenciFacadeApiFeign;

    @Override
    protected void translateLang(PublishContext context) {
        cutWords(context);
        super.translateLang(context);
    }

    @Override
    protected void doPrepare(PublishContext context) {
        truncateContent(context);
        super.doPrepare(context);
    }

    private void cutWords(PublishContext context) {
        String subject = context.getGoods().getSubject();
        var wordsList = fenciFacadeApiFeign.cutWords(subject);
        if (!CollectionUtils.isEmpty(wordsList)) {
            int wordsNum = Math.min(wordsList.size(), 6);
            String newSubject = wordsList.stream().limit(wordsNum).collect(Collectors.joining(""));
            context.getGoods().setSubject(newSubject);
            log.info("Cut subject, {} ==> {}", subject, newSubject);
        }
    }

    private void truncateContent(PublishContext context) {
        String subject = context.getGoods().getSubject();
        if (subject.length() > subjectMaxLen) {
            String newSubject = truncateContentWords(subject, subjectMaxLen);
            context.getGoods().setSubject(newSubject);
            log.info("Truncate subject, {} ==> {}", subject, newSubject);
        }
    }

    private String truncateContentWords(String content, int maxLen) {
        int len = Math.min(maxLen, content.length());
        String tmpContent = content.substring(0, len);
        int idx = tmpContent.lastIndexOf(" ");
        if (idx <= 0) {
            return content;
        }
        return tmpContent.substring(0, idx - 1);
    }
}
