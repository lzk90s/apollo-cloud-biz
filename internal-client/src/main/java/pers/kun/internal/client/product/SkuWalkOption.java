package pers.kun.internal.client.product;

import lombok.Data;
import pers.kun.core.web.model.QueryCond;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-09-23
 */
@Data
public class SkuWalkOption {
    /**
     * 查询条件
     */
    private List<QueryCond> condList;
    /**
     * 数量
     */
    private Integer num;
    /**
     * 最后的游标
     */
    private String lastCursor;
}
