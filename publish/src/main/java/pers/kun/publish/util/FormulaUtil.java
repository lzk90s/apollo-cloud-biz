package pers.kun.publish.util;

import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.QueryCond;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-05-16
 */
public class FormulaUtil {
    private static final String VARIABLE_BEGIN = "${";
    private static final String VARIABLE_END = "}";
    // 行分隔符
    private static final String LINE_SEPARATOR = "---\n";

    public static Map<String, String> loadAssignFormulas(String formulas) {
        var lines = parseLines(formulas).stream()
                .filter(s -> s.contains(QueryCond.OpEnum.ASSIGN.getOp())
                        && !s.contains(QueryCond.OpEnum.NULL_ASSIGN.getOp()))
                .collect(Collectors.toList());

        Map<String, String> result = new LinkedHashMap<>();
        for (var line : lines) {
            var eqIdx = line.indexOf(QueryCond.OpEnum.ASSIGN.getOp());
            if (eqIdx < 0) {
                continue;
            }
            var key = line.substring(0, eqIdx);
            var value = line.substring(eqIdx + QueryCond.OpEnum.ASSIGN.getOp().length());
            result.put(key.strip(), value.strip());
        }
        return result;
    }

    public static Map<String, String> loadNullAssignFormulas(String formulas) {
        var lines = parseLines(formulas).stream()
                .filter(s -> s.contains(QueryCond.OpEnum.NULL_ASSIGN.getOp()))
                .collect(Collectors.toList());

        Map<String, String> result = new LinkedHashMap<>();
        for (var line : lines) {
            var eqIdx = line.indexOf(QueryCond.OpEnum.NULL_ASSIGN.getOp());
            if (eqIdx < 0) {
                continue;
            }
            var key = line.substring(0, eqIdx);
            var value = line.substring(eqIdx + QueryCond.OpEnum.NULL_ASSIGN.getOp().length());
            result.put(key.strip(), value.strip());
        }
        return result;
    }

    public static List<String> loadFilters(String filters) {
        // 解析包含出变量的filter
        return parseLines(filters).stream()
                .filter(s -> s.contains(VARIABLE_BEGIN) && s.contains(VARIABLE_END))
                .map(String::strip)
                .collect(Collectors.toList());
    }

    public static List<String> loadSelectors(String selectors) {
        return parseLines(selectors).stream()
                .filter(s -> s.contains(VARIABLE_BEGIN) && s.contains(VARIABLE_END))
                .map(s -> s.replace(VARIABLE_BEGIN, "").replace(VARIABLE_END, ""))
                .map(String::strip)
                .collect(Collectors.toList());
    }

    public static List<String> parseVarsInExpr(String expr) {
        var strArray = StringUtils.substringsBetween(expr, VARIABLE_BEGIN, VARIABLE_END);
        if (null == strArray || strArray.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(strArray);
    }

    public static String replaceVarValueInExpr(String expr, String var, String value) {
        return expr.replace(VARIABLE_BEGIN + var + VARIABLE_END, value);
    }

    private static List<String> parseLines(String content) {
        if (StringUtils.isEmpty(content)) {
            return Collections.emptyList();
        }
        return Arrays.stream(content.split(LINE_SEPARATOR))
                .map(String::strip)
                .filter(s -> !StringUtils.isEmpty(s))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String formula = "price=${price}*3+5\n" +
                "---\n" +
                "weight?=0.5kg" +
                "---\n" +
                "tmpSubject=${subject}\n" +
                "---\n" +
                "subject=Clothing ${feature.袖长} ${feature.套装件数} ${feature.风格} ${feature.适合年龄段} ${tmpSubject}\n" +
                "---\n" +
                "description=Your little one will love this cute pajamas so much\n" +
                "---\n" +
                "feature.keyFeature1 = 100% Cotton\n" +
                "---\n" +
                "feature.keyFeature2 = Pull On closure\n" +
                "---\n" +
                "feature.keyFeature3 = Machine Wash\n" +
                "---\n" +
                "feature.keyFeature4 = Imported\n" +
                "---\n" +
                "feature.keyFeature5 = Comfy feeling:Breathable and soft cotton ensure comfortable natural night sleep\n" +
                "\n";

        Map map = FormulaUtil.loadAssignFormulas(formula);
        for (var entry : map.entrySet()) {
            System.out.println(entry);
        }

        Map<String, String> map1 = FormulaUtil.loadNullAssignFormulas(formula);
        for (var entry : map1.entrySet()) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }

        List list = FormulaUtil.loadFilters("{price}>20 ------\n {cout} > 30 ------\n");
        String jsonList = "[\"{price}>20\",\"{cout} > 30\"]";

        String s = "{price}*3+5";
        System.out.println(CalculateUtil.isMathExpr(s));
        System.out.println(parseVarsInExpr(s));
        System.out.println(replaceVarValueInExpr(s, "price", "1.2"));
    }
}
