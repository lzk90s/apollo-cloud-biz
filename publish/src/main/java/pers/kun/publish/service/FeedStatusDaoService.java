package pers.kun.publish.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.kun.publish.dao.entity.FeedStatusDO;
import pers.kun.publish.dao.mapper.FeedStatusMapper;

@Service
public class FeedStatusDaoService extends ServiceImpl<FeedStatusMapper, FeedStatusDO> {
}
