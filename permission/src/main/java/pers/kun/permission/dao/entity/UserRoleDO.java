package pers.kun.permission.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
@TableName("t_user_role")
public class UserRoleDO {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String tenantId;
    private String roleId;
    private Integer status;
}
