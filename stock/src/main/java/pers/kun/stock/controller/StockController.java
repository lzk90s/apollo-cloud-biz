//package pers.kun.stock.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pers.kun.common.rest.Result;
//import pers.kun.common.web.BaseController;
//import pers.kun.common.web.model.PageData;
//import pers.kun.common.web.model.unsupported.UnsupportedAddCmd;
//import pers.kun.common.web.model.unsupported.UnsupportedImportCmd;
//import pers.kun.common.web.model.unsupported.UnsupportedUpdateCmd;
//import pers.kun.stock.dao.entity.StockEntity;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author : qihang.liu
// * @date 2021-09-05
// */
//@RestController
//@RequestMapping("/stock")
//public class StockController extends BaseController<
//        ProductMapper,
//        StockEntity,
//        ProductVO,
//        UnsupportedAddCmd<StockEntity>,
//        UnsupportedUpdateCmd<StockEntity>,
//        ProductPageQuery,
//        UnsupportedImportCmd<StockEntity>
//        > {
//    @Autowired
//    private ProductSkuDaoService productSkuDaoService;
//
//    public StockController(@Autowired ProductDaoService daoService) {
//        super(daoService, StockEntity.class, ProductVO.class, null, null);
//    }
//
//    @Override
//    public Result<PageData<ProductVO>> getPage(ProductPageQuery req) {
//        var result = super.getPage(req);
//        if (!result.isOk()) {
//            return result;
//        }
//
//        if (CollectionUtils.isEmpty(result.getResult().getData())) {
//            return Result.ok(new PageData<>(0L, 0L, null));
//        }
//        var list = result.getResult().getData().stream()
//                .peek(s -> {
//                    var skuList = productSkuDaoService.list(new QueryWrapper<ProductSkuEntity>()
//                            .eq("goods_id", s.getId()))
//                            .stream().map(k -> SkuVO.convertGoodsSkuEntity().s2t(k))
//                            .collect(Collectors.toList());
//                    s.setSkuList(skuList);
//                })
//                .collect(Collectors.toList());
//        return Result.ok(new PageData<>(result.getResult().getPageNo(), result.getResult().getTotalCount(), list));
//    }
//
//    @GetMapping("/category")
//    public Result<List<String>> listCategory() {
//        var list = daoService.list(new QueryWrapper<StockEntity>().groupBy("category"));
//        if (CollectionUtils.isEmpty(list)) {
//            return Result.ok();
//        }
//        var resList = list.stream().map(StockEntity::getCategory).collect(Collectors.toList());
//        return Result.ok(resList);
//    }
//
//    @GetMapping("/platform")
//    public Result<List<String>> listPlatform() {
//        var list = daoService.list(new QueryWrapper<StockEntity>().groupBy("platform"));
//        if (CollectionUtils.isEmpty(list)) {
//            return Result.ok();
//        }
//        var resList = list.stream().map(StockEntity::getPlatform).collect(Collectors.toList());
//        return Result.ok(resList);
//    }
//}
