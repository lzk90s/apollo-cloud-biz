package pers.kun.publish.util;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.kun.core.util.JsonUtil;
import pers.kun.internal.client.product.ProductDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-09-13
 */
public class VarsPoolUtil {
    public static Map<String, Object> buildVarsPool(Map<String, Object> originalVarsPool, Object obj) {
        if (null == originalVarsPool) {
            originalVarsPool = new HashMap<>();
        }
        originalVarsPool.putAll(object2Map(obj));
        return originalVarsPool;
    }

    public static <T> T mergeObject(T obj, Class<T> objClass, Map<String, Object> formulas) {
        if (CollectionUtils.isEmpty(formulas)) {
            return obj;
        }
        var varsPool = buildVarsPool(null, obj);
        for (var formula : formulas.entrySet()) {
            setVarsPoolValue(varsPool, formula.getKey(), formula.getValue());
        }
        return JsonUtil.map2pojo(varsPool, objClass);
    }

    public static Object getVarsPoolValue(Map<String, Object> varsPool, String varName) {
        Object value = null;
        var names = varName.split("\\.");
        Map tmpMap = varsPool;
        for (int i = 0; i < names.length; i++) {
            String currName = names[i];
            if (!tmpMap.containsKey(currName)) {
                return null;
            }
            value = tmpMap.get(currName);
            if (value instanceof Map) {
                tmpMap = (Map) value;
            }
        }
        return value;
    }

    public static boolean isVarsPoolContainsKey(Map<String, Object> varsPool, String varName) {
        var names = varName.split("\\.");
        Map tmpMap = varsPool;
        String currName = varName;
        for (int i = 0; i < names.length - 1; i++) {
            currName = names[i];
            if (!tmpMap.containsKey(currName)) {
                return false;
            }
            Object value = tmpMap.get(currName);
            if (value instanceof Map) {
                tmpMap = (Map) value;
            }
        }
        return tmpMap.containsKey(names[names.length - 1]);
    }

    public static boolean isNullOrEmpty(Map<String, Object> varsPool, String varName) {
        if (!isVarsPoolContainsKey(varsPool, varName)) {
            return true;
        }
        var v = getVarsPoolValue(varsPool, varName);
        if (v instanceof String) {
            return StringUtils.isEmpty(v);
        }
        return v == null;
    }

    public static void setVarsPoolValue(Map<String, Object> varsPool, String varName, Object varValue) {
        var names = varName.split("\\.");
        Map tmpMap = varsPool;
        String currName = varName;
        for (int i = 0; i < names.length - 1; i++) {
            currName = names[i];
//            if (!tmpMap.containsKey(currName)) {
//                return;
//            }
            Object value = tmpMap.get(currName);
            if (value instanceof Map) {
                tmpMap = (Map) value;
            }
        }
        tmpMap.put(names[names.length - 1], varValue);
    }


    private static Map<String, Object> object2Map(Object obj) {
        var map = JsonUtil.json2map(JsonUtil.obj2json(obj));
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyMap();
        }
        return map;
    }

    public static void main(String[] args) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("test", "11");
        ProductDTO dto = new ProductDTO();
        dto.setSubject("1");
        dto.setFeature(feature);
        var varsPool = buildVarsPool(null, dto);
        System.out.println(isVarsPoolContainsKey(varsPool, "s"));
        System.out.println(getVarsPoolValue(varsPool, "feature.xx"));
        setVarsPoolValue(varsPool, "feature.xx", "cc");
        setVarsPoolValue(varsPool, "xx", "zzzz");
        System.out.println(getVarsPoolValue(varsPool, "feature.xx"));
        System.out.println(isNullOrEmpty(varsPool, "subject"));
        var tt = mergeObject(dto, ProductDTO.class, varsPool);
        System.out.println(JsonUtil.obj2json(tt));
    }
}
