package pers.kun.stock.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.kun.stock.dao.entity.StockDO;

/**
 * @author : qihang.liu
 * @date 2022-01-10
 */
@Mapper
public interface StockMapper extends BaseMapper<StockDO> {
}
