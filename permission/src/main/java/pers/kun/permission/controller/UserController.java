package pers.kun.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.exception.BizException;
import pers.kun.core.rest.R;
import pers.kun.permission.dao.entity.RolePermissionDO;
import pers.kun.permission.integration.UserApiFeign;
import pers.kun.permission.model.RoleVO;
import pers.kun.permission.model.UserInfoVO;
import pers.kun.permission.service.RoleDaoService;
import pers.kun.permission.service.RolePermissionDaoService;
import pers.kun.permission.service.UserRoleDaoService;

import java.util.Optional;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserApiFeign userApiFeign;
    @Autowired
    private RoleDaoService roleDaoService;
    @Autowired
    private RolePermissionDaoService rolePermissionDaoService;
    @Autowired
    private UserRoleDaoService userRoleDaoService;

    @PostMapping("/logout")
    public R<Boolean> logout() {
        log.info("User {} logout", UserProvider.getUserName());
        return R.ok(true);
    }

    @GetMapping("/info")
    public R<UserInfoVO> getUserInfo() {
        var userDo = userApiFeign.getByUserName(UserProvider.getUserName());

        var userRsp = Optional.ofNullable(userDo)
                .map(s -> UserInfoVO.convertUserEntity().s2t(s))
                .orElseThrow(() -> new BizException("user not exist"));

        var userRole = userRoleDaoService.getOne(null);
        Optional.ofNullable(userRole).map(s -> {
            var role = roleDaoService.getById(s.getRoleId());
            var rolePermissionList = rolePermissionDaoService.list(new QueryWrapper<RolePermissionDO>()
                    .eq("role_id", s.getRoleId()));
            return RoleVO.getRolePermissionEntity(rolePermissionList).s2t(role);
        }).ifPresent(s -> {
            userRsp.setRoleId(s.getId());
            userRsp.setRole(s);
        });

        return R.ok(userRsp);
    }
}
