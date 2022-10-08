package pers.kun.erp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.base.BaseDO;

/**
 * @author : qihang.liu
 * @date 2022-08-15
 */
@Data
@TableName(value = "t_community", autoResultMap = true)
public class CommunityDO extends BaseDO {
    private String code;
    private String name;
    private String area;
    private String address;
    private Integer deliveryYear;
    private String ownershipType;
    private Integer price;
    private Integer totalHousehold;
}
