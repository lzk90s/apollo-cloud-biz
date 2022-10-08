package pers.kun.operation.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.kun.core.web.TenantQueryMapper;
import pers.kun.operation.dao.entity.FollowSellRuleDO;

@Mapper
public interface FollowSellRuleMapper extends BaseMapper<FollowSellRuleDO>, TenantQueryMapper {
}
