package pers.kun.publish.model;

import lombok.Data;
import pers.kun.core.convert.BeanConverter;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.publish.dao.entity.FeedResult;
import pers.kun.publish.dao.entity.FeedStatusDO;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class FeedStatusVO {

    /**
     * publishid
     */
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;

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
     * 刊登结果详情
     */
    private List<FeedResult> feedResultDetail;

    /**
     * 平台
     */
    private String platform;

    /**
     * 目标平台账号
     */
    private String platformAccount;

    /**
     * 刊登的产品id(t_goods)
     */
    private String goodsId;

    /**
     * 刊登的产品id列表(t_goods_sku)
     */
    private String goodsSkuIds;

    /**
     * 目标平台上的产品id
     */
    private String targetGoodsId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 刊登数据
     */
    private ProductDTO publishData;

    public static BeanConverter<FeedStatusDO, FeedStatusVO> convertFeedStatusEntity() {
        return BeanConverter.of(FeedStatusDO.class, FeedStatusVO.class);
    }
}
