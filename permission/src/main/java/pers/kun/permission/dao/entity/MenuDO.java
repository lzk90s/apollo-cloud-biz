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
@TableName("t_menu")
public class MenuDO {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String name;
    private String path;
    private Long parentId;
    private String meta;
    private String component;
    private String redirect;
}
