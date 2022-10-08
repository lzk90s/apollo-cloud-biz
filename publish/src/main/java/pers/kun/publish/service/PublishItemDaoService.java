package pers.kun.publish.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.kun.publish.dao.entity.PublishItemDO;
import pers.kun.publish.dao.mapper.PublishItemMapper;

@Service
public class PublishItemDaoService extends ServiceImpl<PublishItemMapper, PublishItemDO> {
}
