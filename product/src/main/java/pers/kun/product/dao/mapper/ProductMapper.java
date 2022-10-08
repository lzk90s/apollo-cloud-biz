package pers.kun.product.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.kun.product.dao.entity.ProductDO;

@Mapper
public interface ProductMapper extends BaseMapper<ProductDO> {
}
