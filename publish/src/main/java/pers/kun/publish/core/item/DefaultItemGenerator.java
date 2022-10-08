package pers.kun.publish.core.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.kun.core.util.JsonUtil;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.internal.client.product.ProductSkuDTO;
import pers.kun.internal.client.vendor_integration.TextDTO;
import pers.kun.publish.core.PublishStatusEnum;
import pers.kun.publish.dao.entity.PublishConfigDO;
import pers.kun.publish.dao.entity.PublishItemDO;
import pers.kun.publish.exception.PublishFailException;
import pers.kun.publish.integration.CurrencyFacadeApiFeign;
import pers.kun.publish.integration.GtinFacadeApiFeign;
import pers.kun.publish.integration.TranslateFacadeApiFeign;
import pers.kun.publish.service.PublishItemDaoService;
import pers.kun.publish.util.CalculateUtil;
import pers.kun.publish.util.FormulaUtil;
import pers.kun.publish.util.StringExtUtil;
import pers.kun.publish.util.VarsPoolUtil;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static pers.kun.publish.core.item.ItemGenerator.GENERATOR;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
@Slf4j
@Component("default" + GENERATOR)
public class DefaultItemGenerator implements ItemGenerator {
    protected static final String LANGUAGE = "en_US";
    protected static final String MONEY_UNIT = "dollar";

    @Autowired
    protected PublishItemDaoService publishItemDaoService;
    @Autowired
    protected TranslateFacadeApiFeign translateApiFeign;
    @Autowired
    protected CurrencyFacadeApiFeign currencyFacadeApiFeign;
    @Autowired
    protected GtinFacadeApiFeign gtinFacadeApiFeign;


    @Value("#{'${sensitiveWords:}'.split(',')}")
    private Set<String> sensitiveWords;

    @PostConstruct
    public void init() {
        log.info("sensitive words is {}", sensitiveWords);
    }

    @Override
    public void generate(PublishContext context) {
        log.info("Begin to prepare goods {}", context.getGoods().getId());

        // 前置判断
        if (CollectionUtils.isEmpty(context.getGoods().getSkuList())) {
            log.error("Invalid sku for goods {}", context.getGoods().getId());
            return;
        }

        // 过滤商品
        if (!filter(context)) {
            log.warn("Goods {} has been filtered, url is {}", context.getGoods().getId(),
                    context.getGoods().getDetailUrl());
            return;
        }

        // 校验是否已经刊登过
        if (hasPublished(context.getPublishConfig().getTenantId(), context.getGoods().getId())) {
            log.warn("Goods {} already published, skip", context.getGoods().getId());
            return;
        }

        // 检查合规
        if (!checkConformance(context)) {
            log.warn("Check goods {} conformance failed, url is {}", context.getGoods().getId(),
                    context.getGoods().getDetailUrl());
        }

        // 生成gtin
        generateGtin(context);

        // 翻译
        translateLang(context);

        // 去除敏感词
        trimSensitiveWords(context);

        // 转换单位
        transformUnit(context);

        // 计算公式
        calculateFormula(context);

        // 准备就绪
        doPrepare(context);
    }


    /**
     * 准备
     *
     * @param context
     */
    protected void doPrepare(PublishContext context) {
        // 更新刊登的数据
        context.getPublishRecord().setPublishData(context.getGoods());
        // 更新状态为准备就绪
        context.getPublishRecord().setStatus(PublishStatusEnum.INITIALIZED.getStatus());

        log.info("Prepare goods,  {}", JsonUtil.obj2json(context.getGoods()));

        // 保存记录
        publishItemDaoService.saveOrUpdate(context.getPublishRecord());
    }

    protected boolean checkConformance(PublishContext context) {
        return true;
    }

    protected void generateGtin(PublishContext context) {
        int num = context.getGoods().getSkuList().size();
        var gtinList = gtinFacadeApiFeign.batchAllocate(num);
        if (CollectionUtils.isEmpty(gtinList) || gtinList.size() != num) {
            log.warn("Allocate gtin failed");
            return;
        }
        for (int i = 0; i < context.getGoods().getSkuList().size(); i++) {
            context.getGoods().getSkuList().get(i).setGtin(gtinList.get(i));
        }
    }

    protected void translateLang(PublishContext context) {
        var fromLang = context.getGoods().getLanguage();
        var toLang = getLanguage();

        // 翻译subject
        if (!StringUtils.isEmpty(context.getGoods().getSubject())) {
            String subject = translateApiFeign.translate(fromLang, toLang, new TextDTO(context.getGoods().getSubject()));
            context.getGoods().setSubject(subject);
        }

        // 翻译description
        if (!StringUtils.isEmpty(context.getGoods().getDescription())) {
            String desc = translateApiFeign.translate(fromLang, toLang, new TextDTO(context.getGoods().getDescription()));
            context.getGoods().setDescription(desc);
        }

        // 翻译brand
        if (!StringUtils.isEmpty(context.getGoods().getBrand())) {
            String brand = translateApiFeign.translate(fromLang, toLang, new TextDTO(context.getGoods().getBrand()));
            context.getGoods().setBrand(brand);
        }

        // 翻译features
        if (!CollectionUtils.isEmpty(context.getGoods().getFeature())) {
            Map<String, Object> newMap = new HashMap<>();
            for (var feature : context.getGoods().getFeature().entrySet()) {
                String key = feature.getKey();
                Object value = feature.getValue();
                if (feature.getValue() instanceof String) {
                    value = translateApiFeign.translate(fromLang, toLang, new TextDTO(feature.getValue().toString()));
                }
                newMap.put(key, value);
            }
            context.getGoods().setFeature(newMap);
        }

        // 翻译sku attr
        for (var sku : context.getGoods().getSkuList()) {
            if (CollectionUtils.isEmpty(sku.getSkuFeature())) {
                continue;
            }
            Map<String, Object> newMap = new HashMap<>();
            for (var attr : sku.getSkuFeature().entrySet()) {
                String key = attr.getKey();
                Object value = attr.getValue();
                if (attr.getValue() instanceof String) {
                    value = translateApiFeign.translate(fromLang, toLang, new TextDTO(attr.getValue().toString()));
                }
                newMap.put(key, value);
            }
            sku.setSkuFeature(newMap);
        }

        // 更新语言
        context.getGoods().setLanguage(toLang);
    }

    protected void trimSensitiveWords(PublishContext context) {
        // 去掉敏感词
        for (var words : sensitiveWords) {
            // 替换subject
            context.getGoods().setSubject(StringExtUtil.ignoreCaseReplace(context.getGoods().getSubject(), words, ""));
            // 替换描述
            context.getGoods().setDescription(StringExtUtil.ignoreCaseReplace(context.getGoods().getDescription(), words, ""));
            for (var sku : context.getGoods().getSkuList()) {
                sku.setName(StringExtUtil.ignoreCaseReplace(sku.getName(), words, ""));
            }
        }
    }

    protected void transformUnit(PublishContext context) {
        // 转换货币单位
        for (var s : context.getGoods().getSkuList()) {
            var exchangeRate = currencyFacadeApiFeign.getExchangeRate(s.getPriceUnit(), getCurrencyUnit());
            Optional.ofNullable(exchangeRate).orElseThrow(() -> new PublishFailException("无效汇率"));
            var rate = new BigDecimal(exchangeRate);
            s.setPrice(s.getPrice().divide(rate, 2, RoundingMode.UP));
            s.setPriceUnit(getCurrencyUnit());
        }
    }

    protected boolean filter(PublishContext context) {
        var filters = FormulaUtil.loadFilters(context.getPublishConfig().getFilters());
        if (CollectionUtils.isEmpty(filters)) {
            return true;
        }

        List<ProductSkuDTO> tmpSkuList = new ArrayList<>();
        for (var sku : context.getGoods().getSkuList()) {
            Map<String, Object> varsPool = buildGoodsVarsPool(context.getGoods(), sku);

            for (int j = 0; j < filters.size(); j++) {
                var vars = FormulaUtil.parseVarsInExpr(filters.get(j));
                int finalJ = j;
                vars.stream()
                        .filter(varsPool::containsKey)
                        .forEach(v -> filters.set(finalJ,
                                FormulaUtil.replaceVarValueInExpr(filters.get(finalJ), v, varsPool.get(v).toString())));
            }

            boolean filterFlag = true;
            for (var filter : filters) {
                String result = CalculateUtil.calculate(filter);
                if (!"true".equals(result)) {
                    filterFlag = false;
                    break;
                }
            }

            if (filterFlag) {
                tmpSkuList.add(sku);
            }
        }

        if (tmpSkuList.isEmpty()) {
            return false;
        }
        context.getGoods().setSkuList(tmpSkuList);
        return true;
    }

    protected void calculateFormula(PublishContext context) {
        String formulaStr = context.getPublishConfig().getFormulas();

        // 处理goods的公式
        var result = calculateFormulaInner(formulaStr, context.getGoods(), null);
        context.setGoods(VarsPoolUtil.mergeObject(context.getGoods(), ProductDTO.class, result));

        // 处理sku的公式
        for (int i = 0; i < context.getGoods().getSkuList().size(); i++) {
            var sku = context.getGoods().getSkuList().get(i);
            result = calculateFormulaInner(formulaStr, context.getGoods(), sku);
            context.getGoods().getSkuList().set(i, VarsPoolUtil.mergeObject(sku, ProductSkuDTO.class, result));
        }
    }

    private Map<String, Object> calculateFormulaInner(String formulaStr, ProductDTO productVO, ProductSkuDTO productSkuVO) {
        var assignFormulas = FormulaUtil.loadAssignFormulas(formulaStr);
        var nullAssignFormulas = FormulaUtil.loadNullAssignFormulas(formulaStr);
        if (CollectionUtils.isEmpty(assignFormulas) && CollectionUtils.isEmpty(nullAssignFormulas)) {
            return Collections.emptyMap();
        }

        var varsPool = buildGoodsVarsPool(productVO, productSkuVO);

        Map<String, Object> tmpFormulas = new LinkedHashMap<>(assignFormulas);
        tmpFormulas.putAll(nullAssignFormulas);
        for (var formula : tmpFormulas.entrySet()) {
            // 如果变量不存在于varsPool，忽略
            if (!VarsPoolUtil.isVarsPoolContainsKey(varsPool, formula.getKey())) {
                continue;
            }

            // 如果是空赋值公式，判断原始变量是否为空
            if (nullAssignFormulas.containsKey(formula.getKey())
                    && !VarsPoolUtil.isNullOrEmpty(varsPool, formula.getKey())) {
                log.debug("Ignore null assign formula {}", formula.getKey());
                continue;
            }

            // 替换表达式中的变量为实际的值
            var vars = FormulaUtil.parseVarsInExpr(formula.getValue().toString());
            vars.forEach(v -> {
                var s = Optional.ofNullable(VarsPoolUtil.getVarsPoolValue(varsPool, v)).map(Object::toString).orElse("");
                formula.setValue(FormulaUtil.replaceVarValueInExpr(formula.getValue().toString(), v, s));
            });

            // 如果是数学表达式，计算表达式的值
            if (CalculateUtil.isMathExpr(formula.getValue().toString())) {
                formula.setValue(CalculateUtil.calculate(formula.getValue().toString()));
            }

            // 如果varsPoll里面的字段是map类型，把formula的字符串也转成map
            if (varsPool.get(formula.getKey()) instanceof Map) {
                formula.setValue(JsonUtil.json2map(formula.getValue().toString()));
            }

            // 把新生成的数据加入varsPool，后续的计算可以使用前面的值
            VarsPoolUtil.setVarsPoolValue(varsPool, formula.getKey(), formula.getValue());

            log.debug("Process formula, {} = {}", formula.getKey(), formula.getValue());
        }

        return tmpFormulas;
    }

    private boolean hasPublished(String tenantId, Long goodsId) {
        return publishItemDaoService.count(new QueryWrapper<PublishItemDO>()
                .eq("goods_id", goodsId)
                .eq("tenant_id", tenantId)
                .ne("status", PublishStatusEnum.FAILED.getStatus())) > 0;
    }

    private List<PublishItemDO> getPublishRecordByStatus(PublishConfigDO publishConfig, PublishStatusEnum status) {
        var recordList = publishItemDaoService
                .list(new QueryWrapper<PublishItemDO>()
                        .eq("tenant_id", publishConfig.getTenantId())
                        .eq("publish_config_id", publishConfig.getId())
                        .eq("platform", publishConfig.getDstPlatform())
                        .eq("platform_account", publishConfig.getDstPlatformAccount())
                        .eq("status", status.getStatus()));
        if (CollectionUtils.isEmpty(recordList)) {
            return Collections.emptyList();
        }
        return recordList;
    }

    private Map<String, Object> buildGoodsVarsPool(ProductDTO productVO, ProductSkuDTO skuDTO) {
        Map<String, Object> varsPool = new HashMap<>();
        if (null != productVO) {
            VarsPoolUtil.buildVarsPool(varsPool, productVO);
        }
        if (null != skuDTO) {
            VarsPoolUtil.buildVarsPool(varsPool, skuDTO);
        }
        return varsPool;
    }

    protected String getLanguage() {
        return LANGUAGE;
    }

    protected String getCurrencyUnit() {
        return MONEY_UNIT;
    }
}
