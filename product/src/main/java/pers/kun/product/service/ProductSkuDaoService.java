package pers.kun.product.service;

import org.springframework.stereotype.Service;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.product.dao.entity.ProductSkuDO;
import pers.kun.product.dao.mapper.ProductSkuMapper;

@Service
public class ProductSkuDaoService extends DaoServiceImpl<ProductSkuMapper, ProductSkuDO> {
}
