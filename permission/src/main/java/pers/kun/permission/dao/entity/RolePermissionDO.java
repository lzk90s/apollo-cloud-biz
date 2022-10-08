package pers.kun.permission.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@TableName("t_role_permission")
@Data
public class RolePermissionDO {
    private String roleId;
    private String permissionId;
    private String permissionName;
    private String actions;
}
