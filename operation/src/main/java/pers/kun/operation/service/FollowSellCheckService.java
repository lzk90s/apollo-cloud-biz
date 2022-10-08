package pers.kun.operation.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.kun.core.util.JsonUtil;
import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.internal.client.permission.PlatformAccountDTO;
import pers.kun.operation.dao.entity.FollowSellRuleDO;
import pers.kun.operation.integration.MessageMServiceFeign;
import pers.kun.operation.integration.PlatformAccountMServiceFeign;
import pers.kun.operation.integration.ProductMServiceFeign;
import pers.kun.operation.model.FollowSellInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-12-05
 */
@Service
@Slf4j
public class FollowSellCheckService {
    @Autowired
    private ProductMServiceFeign goodsApiFeign;
    @Autowired
    private PlatformAccountMServiceFeign platformAccountApiFeign;
    @Autowired
    private FollowSellCheckService followSellCheckService;
    @Autowired
    private ReducePriceHandler reducePriceHandler;
    @Autowired
    private MessageMServiceFeign messageApiFeign;

    public void batchCheckPrice(List<FollowSellRuleDO> rules) {
        StringBuilder sb = new StringBuilder();

        for (var rule : rules) {
            checkPrice(rule, sb);
        }

        if (StringUtils.isBlank(sb.toString())) {
            return;
        }

        var notifyMsg = new MessageDTO();
        notifyMsg.setTopic("operation.followSell");
        notifyMsg.setMessageType("markdown");
        notifyMsg.setMessage("# 抢购物车！\n" + sb);
        notifyMsg.setTitle("抢购物车");
        messageApiFeign.sendMessage(notifyMsg);
    }

    private void checkPrice(FollowSellRuleDO rule, StringBuilder notifyMsgBuilder) {
        var sku = goodsApiFeign.getSku(rule.getSkuId());
        if (null == sku) {
            return;
        }

        var accountList = platformAccountApiFeign.listPlatformAccountByTenant(rule.getTenantId());
        if (CollectionUtils.isEmpty(accountList)) {
            return;
        }
        var accountMap = accountList.stream()
                .collect(Collectors.toMap(PlatformAccountDTO::getName, o -> o));

        var followSells = JsonUtil.json2list(sku.getFollowSells(), FollowSellInfo.class);
        if (CollectionUtils.isEmpty(followSells)) {
            return;
        }

        // 过滤出自己
        var mySellInfo = followSells.stream()
                .filter(s -> accountMap.containsKey(s.getSeller()))
                .findFirst()
                .orElse(null);
        var minPriceSellInfo = followSells.stream()
                .filter(s -> !accountMap.containsKey(s.getSeller()))
                .min(Comparator.comparingDouble(FollowSellInfo::getPrice))
                .orElse(null);
        if (null == mySellInfo || null == minPriceSellInfo) {
            return;
        }

        // 降价
        var msg = reducePriceHandler.buildReducePriceMsg(rule, sku, mySellInfo, minPriceSellInfo);
        Optional.ofNullable(msg).filter(StringUtils::isNotBlank).ifPresent(s -> notifyMsgBuilder.append("\n------------\n").append(s));
    }
}
