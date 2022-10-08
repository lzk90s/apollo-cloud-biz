package pers.kun.erp.model;

import lombok.Data;
import pers.kun.core.web.model.BaseAddCmd;
import pers.kun.erp.dao.entity.HandleAddressDO;

import javax.validation.constraints.NotBlank;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
@Data
public class HandleAddressAddCmd extends BaseAddCmd<HandleAddressDO> {
    @NotBlank
    private String name;

    private String detail;
}
