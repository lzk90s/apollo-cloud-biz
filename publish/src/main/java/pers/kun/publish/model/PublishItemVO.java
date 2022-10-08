package pers.kun.publish.model;

import lombok.Data;
import pers.kun.core.convert.BeanConverter;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.publish.dao.entity.PublishItemDO;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class PublishItemVO {
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
     * 刊登的产品id(t_goods)
     */
    private String goodsId;

    /**
     * 刊登数据
     */
    private ProductDTO publishData;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;

    public static BeanConverter<PublishItemDO, PublishItemVO> convertPublishItemEntity() {
        return BeanConverter.of(PublishItemDO.class, PublishItemVO.class);
    }
}
