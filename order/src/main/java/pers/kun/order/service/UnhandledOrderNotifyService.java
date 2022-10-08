package pers.kun.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import pers.kun.core.convert.BeanConverter;
import pers.kun.core.util.JsonUtil;
import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.internal.client.messager.MessageTypeConst;
import pers.kun.internal.client.permission.PlatformAccountDTO;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.internal.client.vendor_integration.OrderDTO;
import pers.kun.order.dao.entity.OrderEntity;
import pers.kun.order.integration.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UnhandledOrderNotifyService {
    private static final String TEMPLATE_NAME = "unhandled-order.html";

    @Autowired
    private PlatformAccountMServiceFeign platformAccountApiFeign;
    @Autowired
    private UserApiFeign userApiFeign;
    @Autowired
    private OrderFacadeApiFeign orderSpiderApiFeign;
    @Autowired
    private OrderDaoService orderDaoService;
    @Autowired
    private MessageMServiceFeign messageApiFeign;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private ProductMServiceFeign goodsApiFeign;

    @Value("${notify.internalHour:3}")
    private int notifyInternalHour;

    @Transactional(rollbackFor = Exception.class)
    public void scanOrder() {
        log.info("Start scan......");

        var accountList = platformAccountApiFeign.listAllUserPlatformAccount();
        if (CollectionUtils.isEmpty(accountList)) {
            log.info("No account, skip");
            return;
        }

        accountList.forEach(this::syncUserUnhandledOrder);
        accountList.forEach(this::notifyUserNewOrder);
    }

    private void syncUserUnhandledOrder(PlatformAccountDTO account) {
        var orderList = orderSpiderApiFeign.listUnhandledOrder(account.getPlatform(), account.getClientId(),
                account.getClientSecret(), account.getCookies());
        if (CollectionUtils.isEmpty(orderList)) {
            orderDaoService.remove(new QueryWrapper<OrderEntity>()
                    .eq("tenant_id", account.getTenantId())
                    .eq("platform_account", account.getPlatformUser()));
            return;
        }

        // 删除不存在的订单
        var orderIdList = orderList.stream().map(OrderDTO::getId).collect(Collectors.toList());
        orderDaoService.remove(new QueryWrapper<OrderEntity>()
                .eq("tenant_id", account.getTenantId())
                .eq("platform_account", account.getPlatformUser())
                .notIn("id", orderIdList));

        var orderEntityList = orderList.stream()
                .map(s -> convertOrderDTO().s2t(s))
                .peek(s -> s.setPlatform(account.getPlatform()))
                .peek(s -> s.setPlatformAccount(account.getPlatformUser()))
                .peek(s -> s.setTenantId(account.getTenantId()))
                .collect(Collectors.toList());
        orderDaoService.saveOrUpdateBatch(orderEntityList);

        log.info("发现用户{}的{}个未处理订单", account.getPlatformUser(), orderEntityList.size());
    }

    private void notifyUserNewOrder(PlatformAccountDTO platformAccountVO) {
        String tenantId = platformAccountVO.getTenantId();
        String platformAccount = platformAccountVO.getPlatformUser();

        if (!hasUnNotifyOrder(tenantId, platformAccount)) {
            return;
        }

        // 查询用户订单
        var orderList = orderDaoService.list(new QueryWrapper<OrderEntity>()
                .eq("tenant_id", tenantId)
                .eq("platform_account", platformAccount)
                .orderByDesc("add_time"));
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        var messageDTO = new MessageDTO("order.unhandledOrder",
                "跨境电商: 未处理订单通知消息",
                MessageTypeConst.TEXT,
                buildNotifyMessage(tenantId, platformAccount, orderList));
        messageApiFeign.sendMessage(messageDTO);

        log.info("通知用户{}下的帐号{}有{}个未处理的订单", tenantId, platformAccount, orderList.size());

        // 更新最后通知时间
        var newOrderList = orderList.stream()
                .peek(s -> s.setLastNotifyTime(new Date()))
                .collect(Collectors.toList());
        orderDaoService.updateBatchById(newOrderList);
    }

    private boolean hasUnNotifyOrder(String user, String platformAccount) {
        return orderDaoService.count(new QueryWrapper<OrderEntity>()
                .eq("tenant_id", user)
                .eq("platform_account", platformAccount)
                .isNull("last_notify_time")) > 0;
    }

    private String buildNotifyMessage(String tenantId, String platformAccount, List<OrderEntity> orderDTOList) {
        if (CollectionUtils.isEmpty(orderDTOList)) {
            return "";
        }

        var userInfo = userApiFeign.getByTenant(tenantId);
        Optional.ofNullable(userInfo).orElseThrow(IllegalArgumentException::new);

        var orderMapList = orderDTOList.stream()
                .map(t -> {
                    var url = Optional.ofNullable(goodsApiFeign.getSku(t.getSku()))
                            .map(s -> goodsApiFeign.getProduct(s.getGoodsId()))
                            .map(ProductDTO::getDetailUrl)
                            .orElse("");
                    var map = JsonUtil.json2map(JsonUtil.obj2json(t));
                    map.put("goodsOriginalUrl", url);
                    return map;
                }).collect(Collectors.toList());
        String title = String.format("您的账户[%s - %s]有%d个未处理订单，请及时处理！", userInfo.getName(), platformAccount, orderDTOList.size());
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("orders", orderMapList);

        try {
            var template = freeMarkerConfig.getConfiguration().getTemplate(TEMPLATE_NAME);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public BeanConverter<OrderDTO, OrderEntity> convertOrderDTO() {
        return BeanConverter.of(OrderDTO.class, OrderEntity.class);
    }
}
