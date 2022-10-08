package pers.kun.publish.model;

import lombok.Data;
import pers.kun.core.web.model.PageQueryOption;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class PublishConfigQuery extends PageQueryOption {
    private String name;
}
