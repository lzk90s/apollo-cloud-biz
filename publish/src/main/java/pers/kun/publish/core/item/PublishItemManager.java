package pers.kun.publish.core.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pers.kun.core.util.SpringContextHolder;
import pers.kun.core.web.model.QueryCond;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.internal.client.product.ProductWalkOption;
import pers.kun.publish.core.PublishRuleLoader;
import pers.kun.publish.core.PublishStatusEnum;
import pers.kun.publish.dao.entity.PublishConfigDO;
import pers.kun.publish.dao.entity.PublishItemDO;
import pers.kun.publish.integration.ProductMServiceFeign;
import pers.kun.publish.util.FormulaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static pers.kun.publish.core.item.ItemGenerator.GENERATOR;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
@Service
@Slf4j
public class PublishItemManager {
    @Autowired
    private ProductMServiceFeign goodsApiFeign;
    @Autowired
    private PublishRuleLoader publishRuleLoader;

    public void generate(PublishConfigDO publishConfig, Consumer<Long> cursorCommitter) {
        var goodsList = pullGoods(publishConfig);

        log.info("Got {} goods with rule {}", goodsList.size(), publishConfig.getName());

        goodsList.forEach(s -> {
            PublishItemDO publishItemDO = new PublishItemDO();
            publishItemDO.setTenantId(publishConfig.getTenantId());
            publishItemDO.setPublishConfigId(publishConfig.getId());
            publishItemDO.setGoodsId(s.getId());
            publishItemDO.setStatus(PublishStatusEnum.FAILED.getStatus());

            var context = PublishContext.builder()
                    .publishConfig(publishConfig)
                    .publishRecord(publishItemDO)
                    .goods(s)
                    .build();

            // generate
            getItemGenerator(publishConfig.getDstPlatform()).generate(context);

            // 更新游标
            cursorCommitter.accept(s.getId());
        });
    }

    private List<ProductDTO> pullGoods(PublishConfigDO publishConfig) {
        ProductWalkOption option = new ProductWalkOption();
        option.setNum(10);
        option.setLastCursor(publishConfig.getPublishCursor());
        var selectors = FormulaUtil.loadSelectors(publishConfig.getSelectors());
        if (!CollectionUtils.isEmpty(selectors)) {
            option.setCondList(selectors.stream().map(QueryCond::parse).collect(Collectors.toList()));
        }
        var result = goodsApiFeign.walkProduct(option);
        return result.getGoodsList();
    }

    public static ItemGenerator getItemGenerator(String platform) {
        try {
            return SpringContextHolder.getBean(platform + GENERATOR);
        } catch (Exception e) {
            return SpringContextHolder.getBean("default" + GENERATOR);
        }
    }
}
