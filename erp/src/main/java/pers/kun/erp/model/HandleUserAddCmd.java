package pers.kun.erp.model;

import lombok.Data;
import pers.kun.core.web.model.BaseAddCmd;
import pers.kun.erp.dao.entity.HandleUserDO;

import javax.validation.constraints.NotBlank;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
@Data
public class HandleUserAddCmd extends BaseAddCmd<HandleUserDO> {
    @NotBlank
    private String name;
}
