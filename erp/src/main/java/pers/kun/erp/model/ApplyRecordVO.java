package pers.kun.erp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pers.kun.core.base.BaseVO;

import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class ApplyRecordVO extends BaseVO {

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date handleTime;

    /**
     * 地点
     */
    private String handleAddress;

    /**
     * 状态
     */
    private Integer status;
}
