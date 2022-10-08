package pers.kun.product.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import pers.kun.core.web.model.QueryCond;
import pers.kun.core.util.JsonUtil;
import pers.kun.product.dao.entity.ProductDO;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-23
 */
public class EntityUtil {
    public static <T> QueryWrapper<T> queryCond2QueryWrapper(Class<T> clz, List<QueryCond> condList) {
        var columns = EntityUtil.parseColumnsByEntity(clz);
        var validCondList = condList.stream().filter(s -> columns.contains(s.getKey())).collect(Collectors.toList());

        var entity = new QueryWrapper<T>();
        validCondList.forEach(s -> {
            var op = QueryCond.OpEnum.get(s.getOp());
            if (null == op) {
                return;
            }
            switch (op) {
                case EQ:
                    entity.eq(s.getKey(), s.getValue());
                    break;
                case GE:
                    entity.ge(s.getKey(), s.getValue());
                    break;
                case GT:
                    entity.gt(s.getKey(), s.getValue());
                    break;
                case LE:
                    entity.le(s.getKey(), s.getValue());
                    break;
                case LT:
                    entity.lt(s.getKey(), s.getValue());
                    break;
                case NE:
                    entity.ne(s.getKey(), s.getValue());
                    break;
                case LIKE:
                    entity.like(s.getKey(), s.getValue());
                    break;
                default:
                    break;
            }
        });
        return entity;
    }

    private static Set<String> parseColumnsByEntity(Class entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .map(s -> camel2Underscore(s.getName()))
                .collect(Collectors.toSet());
    }

    private static String camel2Underscore(String camel) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel);
    }

    public static void main(String[] args) {
        var conds = Arrays.asList(new QueryCond("tt", "==", "aaa"));
        var wrapper = queryCond2QueryWrapper(ProductDO.class, conds);
        System.out.println(JsonUtil.obj2json(wrapper));
    }
}
