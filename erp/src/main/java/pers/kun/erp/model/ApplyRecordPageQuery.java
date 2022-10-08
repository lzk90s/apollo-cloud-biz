package pers.kun.erp.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.erp.dao.entity.ApplyRecordDO;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
@Accessors(chain = true)
public class ApplyRecordPageQuery extends BasePageQuery<ApplyRecordDO> {
    @JsonIgnore
    private String tenantId;
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
    private String handleTime;

    /**
     * 地点
     */
    private String handleAddress;

    /**
     * 状态
     */
    private Integer status;

    @Override
    public QueryWrapper<ApplyRecordDO> buildQueryWrapper() {
        return null;
    }
}
