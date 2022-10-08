package pers.kun.order.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.kun.order.dao.entity.OrderEntity;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
