package pers.kun.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.exception.ItemNotExistException;
import pers.kun.core.rest.R;
import pers.kun.permission.dao.entity.MenuDO;
import pers.kun.permission.integration.UserApiFeign;
import pers.kun.permission.model.MenuTreeNodeVO;
import pers.kun.permission.model.MenuVO;
import pers.kun.permission.service.MenuDaoService;
import pers.kun.permission.service.MenuTreeBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@RestController
public class MenuController {
    @Autowired
    private MenuDaoService menuDaoService;
    @Autowired
    private UserApiFeign userApiFeign;

    @GetMapping("/user/nav")
    public R<List<MenuVO>> getNav() {
        var user = userApiFeign.getByUserName(UserProvider.getUserName());
        if (null == user) {
            throw new ItemNotExistException("user");
        }
        var menuDoList = menuDaoService.list(new QueryWrapper<MenuDO>()
                .like("app", user.getApp()).or().eq("app", ""));
        if (CollectionUtils.isEmpty(menuDoList)) {
            return R.ok();
        }
        return R.ok(MenuVO.convertMenuEntity().s2t(menuDoList));
    }

    @GetMapping("/user/nav-tree")
    public R<List<MenuTreeNodeVO>> getNavTree() {
        var user = userApiFeign.getByUserName(UserProvider.getUserName());
        if (null == user) {
            throw new ItemNotExistException("user");
        }
        var menuDoList = menuDaoService.list(new QueryWrapper<MenuDO>()
                .like("app", user.getApp()).or().eq("app", ""));
        if (CollectionUtils.isEmpty(menuDoList)) {
            return R.ok();
        }

        var menuTreeList = menuDoList.stream()
                .map(s -> MenuTreeNodeVO.convertMenuEntity().s2t(s))
                .collect(Collectors.toList());

        return R.ok(new MenuTreeBuilder().buildByRecursive(menuTreeList));
    }
}
