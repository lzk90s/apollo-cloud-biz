package pers.kun.publish.util;

import org.springframework.util.StringUtils;
import pers.kun.core.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-05-16
 */
public class ExtendDataUtil {
    public static Map<String, Object> extendData2Map(String extendDataStr) {
        if (StringUtils.isEmpty(extendDataStr)) {
            return new HashMap<>();
        }
        return JsonUtil.json2map(extendDataStr);
    }

    public static Map<String, Object> extendData2Map(Map<String, Object> extendMap, String extendDataStr) {
        if (null == extendDataStr || extendDataStr.isEmpty()) {
            return extendMap;
        }
        extendMap.putAll(extendData2Map(extendDataStr));
        return extendMap;
    }

    public static String toExtendDataJson(Map<String, Object> extendDataMap) {
        return JsonUtil.obj2json(extendDataMap);
    }

    public static Map<String, Object> appendExtendData(Map<String, Object> extendMap, String key, Object value) {
        if (null == extendMap) {
            extendMap = new HashMap<>();
        }
        extendMap.put(key, value);
        return extendMap;
    }

    public static String appendExtendData(String extendDataStr, String key, Object value) {
        Map<String, Object> extendMap = extendData2Map(extendDataStr);
        extendMap.put(key, value);
        return toExtendDataJson(extendMap);
    }
}
