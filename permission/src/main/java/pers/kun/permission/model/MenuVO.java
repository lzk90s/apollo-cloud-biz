package pers.kun.permission.model;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import pers.kun.core.convert.BeanConverter;
import pers.kun.core.util.JsonUtil;
import pers.kun.permission.dao.entity.MenuDO;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
public class MenuVO {
    private Long id;
    private String name;
    private String path;
    private Long parentId;
    private MenuMeta meta;
    private String component;
    private String redirect;

    public static BeanConverter<MenuDO, MenuVO> convertMenuEntity() {
        return new BeanConverter<>(MenuDO.class, MenuVO.class) {
            @Override
            public MenuVO doForward(MenuDO source) {
                MenuVO menuVO = new MenuVO();
                BeanUtils.copyProperties(source, menuVO);
                var meta = JsonUtil.json2pojo(source.getMeta(), MenuMeta.class);
                menuVO.setMeta(meta);
                return menuVO;
            }
        };
    }
}
