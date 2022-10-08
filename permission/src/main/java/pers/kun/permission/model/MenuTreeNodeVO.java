package pers.kun.permission.model;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import pers.kun.core.convert.BeanConverter;
import pers.kun.core.util.JsonUtil;
import pers.kun.permission.dao.entity.MenuDO;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2022-04-21
 */
@Data
public class MenuTreeNodeVO {
    private Long id;
    private String name;
    private String path;
    private MenuMeta meta;
    private String component;
    private String redirect;
    private Long parentId;
    private List<MenuTreeNodeVO> children;


    public static BeanConverter<MenuDO, MenuTreeNodeVO> convertMenuEntity() {
        return new BeanConverter<>(MenuDO.class, MenuTreeNodeVO.class) {
            @Override
            public MenuTreeNodeVO doForward(MenuDO source) {
                MenuTreeNodeVO menuVO = new MenuTreeNodeVO();
                BeanUtils.copyProperties(source, menuVO);
                var meta = JsonUtil.json2pojo(source.getMeta(), MenuMeta.class);
                menuVO.setMeta(meta);
                menuVO.setChildren(null);
                return menuVO;
            }
        };
    }
}
