package pers.kun.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.product.dao.entity.ProductDO;
import pers.kun.product.dao.mapper.ProductMapper;

@Service
@Transactional
public class ProductDaoService extends DaoServiceImpl<ProductMapper, ProductDO> {
}
