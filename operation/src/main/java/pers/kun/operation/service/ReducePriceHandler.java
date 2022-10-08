package pers.kun.operation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.kun.core.exception.ItemNotExistException;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.internal.client.product.ProductSkuDTO;
import pers.kun.operation.dao.entity.FollowSellRuleDO;
import pers.kun.operation.integration.ProductMServiceFeign;
import pers.kun.operation.model.FollowSellInfo;

import java.util.Optional;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Slf4j
@Component
public class ReducePriceHandler {
    @Autowired
    private ProductMServiceFeign goodsApiFeign;

    public String buildReducePriceMsg(FollowSellRuleDO rule, ProductSkuDTO sku, FollowSellInfo mySellInfo, FollowSellInfo minPriceSellInfo) {
        if (null == mySellInfo || null == minPriceSellInfo) {
            return null;
        }

        float myPrice = mySellInfo.getPrice();
        float minPrice = minPriceSellInfo.getPrice();
        float newPrice = minPriceSellInfo.getPrice() - rule.getPriceStep();

        // 如果最小的价格是自己的，说明自己的价格已经是最低的了
        if (myPrice <= minPrice) {
            log.info("My price is lowest, price={}, name={}", mySellInfo.getPrice(), mySellInfo.getSeller());
            return null;
        }

        // 如果价格已经最低了，不能再降了
        if (minPrice <= rule.getMinPrice()) {
            log.info("Min price reached, rulePrice={}, minPrice={}", rule.getMinPrice(), minPriceSellInfo.getPrice());
            return null;
        }

        log.info("Reduce price from {} to {}, minPrice={}, rulePriceStep={}",
                myPrice, newPrice, minPrice, rule.getPriceStep());

        return buildNotifyMsg(sku, mySellInfo.getSeller(), myPrice, minPrice, newPrice);
    }

    private String buildNotifyMsg(ProductSkuDTO sku, String mySeller, Float myPrice, Float minPrice, Float newPrice) {
        var goodsInfo = getGoodsInfo(sku.getGoodsId());
        return "- 商品链接：[点我跳转](" + goodsInfo.getDetailUrl() + ")\n" +
                "- 商品名：" + goodsInfo.getSubject() + "\n" +
                "- 当前账号：" + mySeller + "\n" +
                "- 当前价格：" + myPrice + "\n" +
                "- 当前最低价格：" + minPrice + "\n" +
                "- 调价后价格：" + newPrice + "\n";
    }

    private ProductDTO getGoodsInfo(Long goodsId) {
        return Optional.ofNullable(goodsApiFeign.getProduct(goodsId)).orElseThrow(ItemNotExistException::new);
    }
}
