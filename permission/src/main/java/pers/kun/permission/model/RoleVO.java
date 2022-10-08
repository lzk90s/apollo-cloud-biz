package pers.kun.permission.model;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import pers.kun.permission.dao.entity.RoleDO;
import pers.kun.permission.dao.entity.RolePermissionDO;
import pers.kun.core.convert.BeanConverter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
public class RoleVO {
    private String id;
    private String name;
    private String describe;
    private Integer status;
    private String creatorId;
    private String createTime;
    private List<PermissionAction> permissions;

    public static BeanConverter<RoleDO, RoleVO> getRolePermissionEntity(List<RolePermissionDO> rolePermissionDO) {
        return new BeanConverter<>(RoleDO.class, RoleVO.class) {
            @Override
            public RoleVO doForward(RoleDO source) {
                RoleVO roleVO = new RoleVO();
                BeanUtils.copyProperties(source, roleVO);
                var permissionList = rolePermissionDO.stream()
                        .map(s -> PermissionAction.convertRolePermissionEntity().s2t(s))
                        .collect(Collectors.toList());
                roleVO.setPermissions(permissionList);
                return roleVO;
            }
        };
    }
}
