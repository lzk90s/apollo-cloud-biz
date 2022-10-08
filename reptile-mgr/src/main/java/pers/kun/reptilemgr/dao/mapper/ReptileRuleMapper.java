package pers.kun.reptilemgr.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.kun.core.web.TenantQueryMapper;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Mapper
public interface ReptileRuleMapper extends BaseMapper<ReptileRuleDO>, TenantQueryMapper {
}
