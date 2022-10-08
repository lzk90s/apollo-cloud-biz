package pers.kun.erp.model;

import lombok.Data;
import pers.kun.core.base.BaseVO;

/**
 * @author : qihang.liu
 * @date 2022-08-15
 */
@Data
public class CommunityVO extends BaseVO {
    private Long id;
    private String code;
    private String name;
    private String area;
    private String address;
    private Integer deliveryYear;
    private String ownershipType;
    private Integer price;
    private Integer totalHousehold;
}
