package pers.kun.permission.model;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import pers.kun.permission.dao.entity.RolePermissionDO;
import pers.kun.core.convert.BeanConverter;
import pers.kun.core.util.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
public class PermissionAction {
    private String roleId;
    private String permissionId;
    private String permissionName;
    private String actions;
    private List<Map<String, Object>> actionEntitySet;

    public static BeanConverter<RolePermissionDO, PermissionAction> convertRolePermissionEntity() {
        return new BeanConverter<>(RolePermissionDO.class, PermissionAction.class) {
            @Override
            public PermissionAction doForward(RolePermissionDO source) {
                PermissionAction permissionAction = new PermissionAction();
                BeanUtils.copyProperties(source, permissionAction);
                var actionList = JsonUtil.json2pojo(source.getActions(), List.class);
                permissionAction.setActionEntitySet(actionList);
                return permissionAction;
            }
        };
    }
}
