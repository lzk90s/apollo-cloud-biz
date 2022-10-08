package pers.kun.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.rest.R;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.core.web.model.unsupported.UnsupportedAddCmd;
import pers.kun.core.web.model.unsupported.UnsupportedUpdateCmd;
import pers.kun.product.dao.entity.ProductDO;
import pers.kun.product.dao.mapper.ProductMapper;
import pers.kun.product.model.ProductPageQuery;
import pers.kun.product.model.ProductVO;
import pers.kun.product.service.ProductDaoService;
import pers.kun.product.service.ProductSkuDaoService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@RestController
@RequestMapping("/product")
public class ProductController implements BaseCurdController<
        ProductMapper,
        ProductDO,
        ProductVO,
        UnsupportedAddCmd<ProductDO>,
        UnsupportedUpdateCmd<ProductDO>,
        ProductPageQuery
        > {
    @Autowired
    private ProductSkuDaoService productSkuDaoService;
    @Autowired
    private ProductDaoService productDaoService;

    @Override
    public DaoServiceImpl<ProductMapper, ProductDO> getDaoService() {
        return productDaoService;
    }

    @Override
    public Class<ProductVO> getVoClass() {
        return ProductVO.class;
    }

    @Override
    public Class<ProductDO> getDoClass() {
        return ProductDO.class;
    }

    @GetMapping("/category")
    public R<List<String>> listCategory() {
        var list = getDaoService().list(new QueryWrapper<ProductDO>().groupBy("category"));
        if (CollectionUtils.isEmpty(list)) {
            return R.ok();
        }
        var resList = list.stream().map(ProductDO::getCategory).collect(Collectors.toList());
        return R.ok(resList);
    }

    @GetMapping("/platform")
    public R<List<String>> listPlatform() {
        var list = getDaoService().list(new QueryWrapper<ProductDO>().groupBy("platform"));
        if (CollectionUtils.isEmpty(list)) {
            return R.ok();
        }
        var resList = list.stream().map(ProductDO::getPlatform).collect(Collectors.toList());
        return R.ok(resList);
    }
}
