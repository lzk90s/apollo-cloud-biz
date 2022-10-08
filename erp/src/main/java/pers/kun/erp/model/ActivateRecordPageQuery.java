package pers.kun.erp.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.kun.core.web.model.BasePageQuery;
import pers.kun.erp.dao.entity.ActivateRecordDO;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class ActivateRecordPageQuery extends BasePageQuery<ActivateRecordDO> {
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
     * 是否已办理
     */
    private Boolean applied;


    @Override
    public QueryWrapper<ActivateRecordDO> buildQueryWrapper() {
        var wrapper = new QueryWrapper<ActivateRecordDO>()
                .eq(StringUtils.isNotBlank(user), "user", user)
                .eq(StringUtils.isNotBlank(phone), "phone", phone)
                .eq(StringUtils.isNotBlank(handleUser), "handle_user", handleUser)
                .eq(null != handleTime, "handle_time", handleTime + " 00:00:00")
                .eq(StringUtils.isNotBlank(handleAddress), "handle_address", handleAddress)
                .orderByDesc(true, "add_time");
        if (null != applied) {
            String existsSql = "select 1 from t_apply_record " +
                    "where t_apply_record.user=t_activate_record.user " +
                    "and t_apply_record.handle_address=t_activate_record.handle_address " +
                    "and t_apply_record.tenant_id=t_activate_record.tenant_id " +
                    "and t_apply_record.deleted=false";
            if (applied) {
                wrapper.exists(existsSql);
            } else {
                wrapper.notExists(existsSql);
            }
        }
        return wrapper;
    }
}
