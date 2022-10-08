package pers.kun.erp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
@Data
@TableName("t_handle_user")
public class HandleUserDO extends BaseTenantDO {
    private String name;
}
