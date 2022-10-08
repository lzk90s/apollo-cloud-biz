package pers.kun.publish.model;

import lombok.Data;
import pers.kun.core.web.model.PageQueryOption;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class FeedStatusQuery extends PageQueryOption {
    /**
     * publish config id
     */
    private Long publishConfigId;

    /**
     * publish item id
     */
    private Long publishItemId;

    /**
     * 刊登结果id
     */
    private String feedResultId;

    /**
     * 平台
     */
    private String platform;

    /**
     * 目标平台账号
     */
    private String platformAccount;

    private String goodsId;

    /**
     * 状态
     */
    private Integer status;
}
