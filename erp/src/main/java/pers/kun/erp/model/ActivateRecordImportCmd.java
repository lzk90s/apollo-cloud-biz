package pers.kun.erp.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import pers.kun.core.web.model.BaseImportCmd;
import pers.kun.erp.dao.entity.ActivateRecordDO;

import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class ActivateRecordImportCmd extends BaseImportCmd<ActivateRecordDO> {

    /**
     * 名字
     */
    @ExcelProperty(value = "姓名", index = 1)
    private String user;

    /**
     * 手机
     */
    @ExcelProperty(value = "手机", index = 2)
    private String phone;

    /**
     * 经办人
     */
    @ExcelIgnore
    private String handleUser;

    /**
     * 办理时间
     */
    @ExcelIgnore
    private Date handleTime;

    /**
     * 地点
     */
    @ExcelIgnore
    private String handleAddress;
}
