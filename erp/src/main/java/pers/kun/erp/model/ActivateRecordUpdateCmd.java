package pers.kun.erp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pers.kun.core.web.model.BaseUpdateCmd;
import pers.kun.erp.dao.entity.ActivateRecordDO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2021-11-23
 */
@Data
public class ActivateRecordUpdateCmd extends BaseUpdateCmd<ActivateRecordDO> {

    @NotNull
    private Long id;

    /**
     * 名字
     */
    @NotBlank
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
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date handleTime;

    /**
     * 地点
     */
    @NotBlank
    private String handleAddress;
}
