package pers.kun.operation.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.web.model.QueryCond;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.internal.client.product.ProductWalkOption;
import pers.kun.operation.dao.entity.FollowSellRuleDO;
import pers.kun.operation.integration.ProductMServiceFeign;
import pers.kun.operation.integration.ReptileRuleMServiceFeign;
import pers.kun.operation.service.FollowSellRuleDaoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@RestController
@RequestMapping("/tasks/follow_sell_rule_create_task")
public class FollowSellRuleCreateTask {
    @Autowired
    private ProductMServiceFeign goodsApiFeign;
    @Autowired
    private ReptileRuleMServiceFeign reptileRuleApiFeign;
    @Autowired
    private FollowSellRuleDaoService followSellRuleDaoService;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    @GetMapping("/exec")
    public void exec() {
        var ruleList = reptileRuleApiFeign.getRuleByLabelsAllTenant("follow_sell");
        if (CollectionUtils.isEmpty(ruleList)) {
            return;
        }

        for (var rule : ruleList) {
            // 设置租户id
            UserProvider.setLocalTenant(rule.getTenantId());

            // 获取最新的游标
            Long cursor = null;
            var lastRule = followSellRuleDaoService.getOne(new QueryWrapper<FollowSellRuleDO>().orderByDesc("add_time"));
            if (null != lastRule) {
                var goods = goodsApiFeign.getProduct(lastRule.getGoodsId());
                cursor = Optional.ofNullable(goods).map(ProductDTO::getId).orElse(null);
            }

            // 根据采集规则id来获取商品
            ProductWalkOption option = new ProductWalkOption();
            option.setNum(200);
            Optional.ofNullable(cursor).ifPresent(option::setLastCursor);
            option.setCondList(Collections.singletonList(new QueryCond("reptile_rule_id", QueryCond.OpEnum.EQ, rule.getId().toString())));
            var result = goodsApiFeign.walkProduct(option);
            if (null == result || CollectionUtils.isEmpty(result.getGoodsList())) {
                return;
            }

            for (var goods : result.getGoodsList()) {
                List<FollowSellRuleDO> entityList = new ArrayList<>();
                for (var sku : goods.getSkuList()) {
                    var ruleDo = followSellRuleDaoService.getOne(new QueryWrapper<FollowSellRuleDO>()
                            .eq("platform", goods.getPlatform())
                            .eq("goods_id", goods.getId())
                            .eq("sku_id", sku.getSkuId()));
                    if (ruleDo != null) {
                        continue;
                    }
                    FollowSellRuleDO entity = new FollowSellRuleDO();
                    entity.setGoodsId(goods.getId());
                    entity.setSkuId(sku.getSkuId());
                    entity.setPlatform(goods.getPlatform());
                    entity.setStatus(0);
                    entity.setPriceStep(0.0f);
                    entity.setMinPrice(0.0f);
                    entity.setTenantId(rule.getTenantId());
                    entityList.add(entity);
                }
                followSellRuleDaoService.saveOrUpdateBatch(entityList);
            }
        }
    }
}
