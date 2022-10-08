package pers.kun.product.facade;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.convert.BeanMutualConverter;
import pers.kun.core.exception.BizException;
import pers.kun.internal.client.product.*;
import pers.kun.product.dao.entity.ProductDO;
import pers.kun.product.dao.entity.ProductSkuDO;
import pers.kun.product.service.ProductDaoService;
import pers.kun.product.service.ProductSkuDaoService;
import pers.kun.product.util.EntityUtil;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/product")
public class ProductMServiceImpl implements ProductMService {
    @Autowired
    private ProductDaoService productDaoService;

    @Autowired
    private ProductSkuDaoService productSkuDaoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(@Valid ProductDTO productVO) {
        productDaoService.saveOrUpdate(convertGoodsDTO().t2s(productVO));
        var list = productVO.getSkuList().stream()
                .map(s -> convertGoodsSkuDTO().t2s(s))
                .peek(s -> s.setProductId(productVO.getId()))
                .peek(s -> s.setReptileRuleId(productVO.getReptileRuleId()))
                .collect(Collectors.toList());
        productSkuDaoService.saveOrUpdateBatch(list);
    }

    @Override
    public ProductDTO getProduct(Long productId) {
        var goodsDto = convertGoodsDTO().s2t(productDaoService.getById(productId));
        if (null == goodsDto) {
            return null;
        }
        var skuList = productSkuDaoService
                .list(new QueryWrapper<ProductSkuDO>().eq("goods_id", productId));
        if (!CollectionUtils.isEmpty(skuList)) {
            var skuDtoList = skuList.stream()
                    .map(s -> convertGoodsSkuDTO().s2t(s)).collect(Collectors.toList());
            goodsDto.setSkuList(skuDtoList);
        }
        return goodsDto;
    }

    @Override
    public ProductSkuDTO getSku(String skuId) {
        return Optional.ofNullable(productSkuDaoService.getById(skuId))
                .map(s -> convertGoodsSkuDTO().s2t(s)).orElse(null);
    }

    @Override
    public boolean isProductExists(String productId) {
        return productDaoService.count(new QueryWrapper<ProductDO>().eq("id", productId)) > 0;
    }

    @Override
    public ProductWalkResult walkProduct(ProductWalkOption option) {
        if (option.getNum() > 200) {
            throw new BizException("Request num over flow");
        }
        if (CollectionUtils.isEmpty(option.getCondList())) {
            throw new BizException("Cond list is empty");
        }

        // 生成查询条件
        var entity = EntityUtil.queryCond2QueryWrapper(ProductDO.class, option.getCondList());
        // 根据游标查询
        if (!StringUtils.isEmpty(option.getLastCursor())) {
            entity.gt("id", option.getLastCursor());
        }
        entity.orderByAsc("idx");

        var pageResult = productDaoService.page(new Page<>(0, option.getNum()), entity);
        if (pageResult.getTotal() <= 0) {
            return new ProductWalkResult("", Collections.emptyList());
        }

        Function<Long, List<ProductSkuDO>> skuProvider = (s) -> {
            var skuList = productSkuDaoService.list(new QueryWrapper<ProductSkuDO>().eq("goods_id", s));
            return CollectionUtils.isEmpty(skuList) ? Collections.emptyList() : skuList;
        };

        var result = pageResult.getRecords().stream()
                .map(s -> convertGoodsDTO().s2t(s))
                .peek(s -> s.setSkuList(skuProvider.apply(s.getId()).stream()
                        .map(k -> convertGoodsSkuDTO().s2t(k))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        String cursor = pageResult.getRecords().get(pageResult.getRecords().size() - 1).getId().toString();

        return new ProductWalkResult(cursor, result);
    }

    @Override
    public SkuWalkResult walkSku(SkuWalkOption option) {
        return null;
    }


    public BeanMutualConverter<ProductDO, ProductDTO> convertGoodsDTO() {
        return BeanMutualConverter.of(ProductDO.class, ProductDTO.class);
    }

    public BeanMutualConverter<ProductSkuDO, ProductSkuDTO> convertGoodsSkuDTO() {
        return BeanMutualConverter.of(ProductSkuDO.class, ProductSkuDTO.class);
    }
}
