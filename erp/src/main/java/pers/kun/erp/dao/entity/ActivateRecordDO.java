package pers.kun.erp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
@TableName(value = "t_activate_record", autoResultMap = true)
public class ActivateRecordDO extends BaseTenantDO {

    /**
     * 名字
     */
    private String user;

    /**
     * 手机
     */
    private String phone;

    /**
     * 经办人
     */
    private String handleUser;

    /**
     * 办理时间
     */
    private Date handleTime;

    /**
     * 地点
     */
    private String handleAddress;

    /**
     * 是否被删除
     */
    private Boolean deleted;
}
