package pers.kun.erp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pers.kun.core.validate.EnumValidator;
import pers.kun.core.web.model.BaseUpdateCmd;
import pers.kun.erp.dao.entity.ApplyRecordDO;
import pers.kun.erp.enums.ApplyStatusEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class ApplyRecordUpdateCmd extends BaseUpdateCmd<ApplyRecordDO> {
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
    @NotBlank
    private String phone;

    /**
     * 经办人
     */
    @NotBlank
    private String handleUser;

    /**
     * 办理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date handleTime;

    /**
     * 地点
     */
    @NotBlank
    private String handleAddress;

    /**
     * 状态
     */
    @EnumValidator(validateClass = ApplyStatusEnum.class, validateMethod = "getByCode")
    private Integer status;
}
