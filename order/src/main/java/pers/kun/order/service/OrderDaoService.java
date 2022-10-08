package pers.kun.order.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.kun.order.dao.entity.OrderEntity;
import pers.kun.order.dao.mapper.OrderMapper;

@Service
public class OrderDaoService extends ServiceImpl<OrderMapper, OrderEntity> {
}
