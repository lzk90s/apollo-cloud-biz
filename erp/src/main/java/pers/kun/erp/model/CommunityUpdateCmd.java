package pers.kun.erp.model;

import lombok.Data;
import pers.kun.core.web.model.BaseUpdateCmd;
import pers.kun.erp.dao.entity.CommunityDO;

/**
 * @author : qihang.liu
 * @date 2022-08-15
 */
@Data
public class CommunityUpdateCmd extends BaseUpdateCmd<CommunityDO> {
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
