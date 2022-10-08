package pers.kun.erp.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import pers.kun.core.web.model.BaseImportCmd;
import pers.kun.erp.dao.entity.ApplyRecordDO;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class ApplyRecordImportCmd extends BaseImportCmd<ApplyRecordDO> {
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
}
