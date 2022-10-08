package pers.kun.permission.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
@TableName("t_role")
public class RoleDO {
    @TableId(type = IdType.INPUT)
    private String id;
    private String name;
    @TableField(value = "`describe`")
    private String describe;
    private Integer status;
}
