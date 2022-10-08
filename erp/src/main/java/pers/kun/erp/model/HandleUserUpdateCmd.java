package pers.kun.erp.model;

import lombok.Data;
import pers.kun.core.web.model.BaseUpdateCmd;
import pers.kun.erp.dao.entity.HandleUserDO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
@Data
public class HandleUserUpdateCmd extends BaseUpdateCmd<HandleUserDO> {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    private String detail;
}
