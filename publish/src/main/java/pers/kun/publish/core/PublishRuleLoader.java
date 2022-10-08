package pers.kun.publish.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pers.kun.publish.dao.entity.PublishConfigDO;
import pers.kun.publish.service.PublishConfigDaoService;

import java.util.Collections;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-05-24
 */
@Component
public class PublishRuleLoader {
    @Autowired
    private PublishConfigDaoService publishConfigDaoService;

    public List<PublishConfigDO> loadPublishConfig() {
        var configList = publishConfigDaoService.list(new QueryWrapper<PublishConfigDO>()
                .eq("status", 1)
                .isNotNull("selectors")
                .ne("selectors", ""));
        if (CollectionUtils.isEmpty(configList)) {
            return Collections.emptyList();
        }
        return configList;
    }

    public PublishConfigDO getPublishConfig(String id) {
        return publishConfigDaoService.getById(id);
    }

    public void commitCursor(Long id, Long cursor) {
        PublishConfigDO entity = new PublishConfigDO();
        entity.setId(id);
        entity.setPublishCursor(cursor);
        publishConfigDaoService.updateById(entity);
    }
}
