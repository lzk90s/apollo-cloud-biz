package pers.kun.publish.model;

import lombok.Data;
import pers.kun.core.web.model.PageQueryOption;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class PublishItemQuery extends PageQueryOption {
    private Long id;
    private String publishConfigId;
    private String goodsId;
    private Integer status;
    private String addTime;
}
