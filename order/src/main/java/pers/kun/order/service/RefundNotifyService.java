package pers.kun.order.service;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.internal.client.messager.MessageTypeConst;
import pers.kun.internal.client.permission.PlatformAccountDTO;
import pers.kun.internal.client.vendor_integration.RefundOrderDTO;
import pers.kun.order.integration.MessageMServiceFeign;
import pers.kun.order.integration.OrderFacadeApiFeign;
import pers.kun.order.integration.PlatformAccountMServiceFeign;
import pers.kun.order.integration.UserApiFeign;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : qihang.liu
 * @date 2021-05-18
 */
@Slf4j
@Service
public class RefundNotifyService {
    private static final String TEMPLATE_NAME = "refund-order.html";

    @Autowired
    private OrderFacadeApiFeign orderFacadeApiFeign;
    @Autowired
    private PlatformAccountMServiceFeign platformAccountApiFeign;
    @Autowired
    private MessageMServiceFeign messageApiFeign;
    @Autowired
    private UserApiFeign userApiFeign;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    public void scanRefund() {
        log.info("Start scan......");

        var accountList = platformAccountApiFeign.listAllUserPlatformAccount();
        if (CollectionUtils.isEmpty(accountList)) {
            log.info("No account, skip");
            return;
        }

        accountList.forEach(this::syncRefundOrder);
    }


    private void syncRefundOrder(PlatformAccountDTO account) {
        var orderList = orderFacadeApiFeign.listRefundOrder(account.getPlatform(),
                account.getClientId(), account.getClientSecret(), account.getCookies());
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }
        notifyUserRefundOrder(account, orderList);
    }


    private void notifyUserRefundOrder(PlatformAccountDTO platformAccountVO, List<RefundOrderDTO> orderList) {
        String tenantId = platformAccountVO.getTenantId();
        String platformAccount = platformAccountVO.getPlatformUser();

        var userInfo = userApiFeign.getByTenant(tenantId);
        var messageDTO = new MessageDTO("order.unhandledOrder",
                "跨境电商: 未处理退款通知消息",
                MessageTypeConst.TEXT,
                buildNotifyMessage(tenantId, platformAccount, orderList));
        messageApiFeign.sendMessage(messageDTO);

        log.info("通知用户{}下的帐号{}有{}个未处理的退款", tenantId, platformAccount, orderList.size());
    }

    private String buildNotifyMessage(String tenantId, String platformAccount, List<RefundOrderDTO> orderDTOList) {
        if (CollectionUtils.isEmpty(orderDTOList)) {
            return "";
        }

        var userInfo = userApiFeign.getByTenant(tenantId);
        Optional.ofNullable(userInfo).orElseThrow(IllegalArgumentException::new);

        String title = String.format("您的账户[%s - %s]有%d个未处理退款，请及时处理！", userInfo.getName(), platformAccount, orderDTOList.size());
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("orders", orderDTOList);

        try {
            var template = freeMarkerConfig.getConfiguration().getTemplate(TEMPLATE_NAME);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
