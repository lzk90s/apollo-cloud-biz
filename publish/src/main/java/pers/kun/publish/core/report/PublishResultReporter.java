package pers.kun.publish.core.report;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import pers.kun.core.util.JsonUtil;
import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.internal.client.messager.MessageTypeConst;
import pers.kun.internal.client.uaa.UserInfoDTO;
import pers.kun.publish.core.PublishStatusEnum;
import pers.kun.publish.dao.entity.FeedStatusDO;
import pers.kun.publish.dao.entity.PublishConfigDO;
import pers.kun.publish.integration.MessageMServiceFeign;
import pers.kun.publish.integration.PlatformAccountMServiceFeign;
import pers.kun.publish.integration.ProductMServiceFeign;
import pers.kun.publish.integration.UserApiFeign;
import pers.kun.publish.service.FeedStatusDaoService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-05-23
 */
@Slf4j
@Component
@Deprecated
public class PublishResultReporter {
    private static final String SEPARATOR = ",";
    private static final String TEMPLATE_NAME = "publish-statistic.html";

    @Autowired
    private PlatformAccountMServiceFeign platformAccountApiFeign;
    @Autowired
    private UserApiFeign userApiFeign;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private MessageMServiceFeign messageApiFeign;
    @Autowired
    private FeedStatusDaoService feedStatusDaoService;
    @Autowired
    private ProductMServiceFeign goodsApiFeign;
    @Value("${externalBaseUrl:}")
    private String externalBaseUrl;

    public void report(PublishConfigDO publishConfig) {
        // 获取上传成功的记录
        var recordList = feedStatusDaoService
                .list(new QueryWrapper<FeedStatusDO>()
                        .eq("tenant_id", publishConfig.getTenantId())
                        .eq("publish_config_id", publishConfig.getId())
                        .eq("platform", publishConfig.getDstPlatform())
                        .eq("platform_account", publishConfig.getDstPlatformAccount())
                        .eq("status", PublishStatusEnum.UPLOADED.getStatus()));
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        notifyUser(publishConfig, recordList);
    }

    private void notifyUser(PublishConfigDO publishConfig, List<FeedStatusDO> publishRecordList) {
        var userList = new ArrayList<UserInfoDTO>();
        var accounts = publishConfig.getDstPlatformAccount().split(SEPARATOR);
        for (var ac : accounts) {
            var account = platformAccountApiFeign.getTenantPlatformAccount(publishConfig.getTenantId(), ac);
            String tenantId = account.getTenantId();
            var userInfo = userApiFeign.getByTenant(tenantId);
            userList.add(userInfo);
        }

        userList.stream()
                .map(UserInfoDTO::getEmail)
                .distinct()
                .forEach(s -> {
                    var messageDTO = new MessageDTO("publish.publishStatistic",
                            "跨境电商: <上架商品统计>",
                            MessageTypeConst.TEXT,
                            buildNotifyMessage(publishConfig.getDstPlatformAccount(), publishConfig, publishRecordList));
                    messageApiFeign.sendMessage(messageDTO);
                });
    }

    private String buildNotifyMessage(String platformAccount, PublishConfigDO publishConfig, List<FeedStatusDO> recordList) {
        String title = String.format("您的账户[%s]有%d个新的上架商品，请核验！", platformAccount, recordList.size());
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("publishConfig", publishConfig);
        var tmpRecordList = recordList.stream()
                .map(s -> {
                    var goods = goodsApiFeign.getProduct(s.getGoodsId());
                    var map = JsonUtil.json2map(JsonUtil.obj2json(goods));
                    map.put("ignoreUrl", buildIgnoreUrl(Collections.singletonList(s.getId().toString())));
                    return map;
                }).collect(Collectors.toList());
        model.put("records", tmpRecordList);
        model.put("reportUrl", buildReportUrl(publishConfig.getId().toString()));
        model.put("confirmUrl", buildConfirmUrl(recordList.stream().map(s -> s.getId().toString()).collect(Collectors.toList())));

        try {
            var template = freeMarkerConfig.getConfiguration().getTemplate(TEMPLATE_NAME);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String buildIgnoreUrl(List<String> publishRecordIds) {
        var ids = String.join(",", publishRecordIds);
        return externalBaseUrl + "/internal/sale_confirm/ignore?ids=" + ids;
    }

    private String buildConfirmUrl(List<String> publishRecordIds) {
        var ids = String.join(",", publishRecordIds);
        return externalBaseUrl + "/internal/sale_confirm/confirm?ids=" + ids;
    }

    private String buildReportUrl(String configId) {
        return externalBaseUrl + "/internal/result_report/report/" + configId;
    }
}
