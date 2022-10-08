package pers.kun.erp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pers.kun.erp.dao.entity.ApplyRecordDO;
import pers.kun.erp.model.ApplyRecordPageQuery;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Mapper
public interface ApplyRecordMapper extends BaseMapper<ApplyRecordDO> {
    String COUNT_SELECT = "count(1) ";

    String QUERY_SELECT = "a.`id`, a.`tenant_id`, a.`user`, a.`phone`, a.`handle_user`, a.`handle_time`, " +
            "a.`handle_address`, a.`add_time`, a.`update_time`, " +
            "case when a.status=1 then 1 when b.id is not null then 2 else 3 end as status ";

    String TABLE = "t_apply_record a left join t_activate_record b on a.user=b.user and a.handle_address=b.handle_address ";

    String QUERY_COND = "<where> " +
            "<when test='q.tenantId!=null and q.tenantId!=\"\"'> and a.tenant_id=#{q.tenantId}</when> " +
            "<when test='q.user!=null and q.user!=\"\"'> and a.user=#{q.user}</when> " +
            "<when test='q.phone!=null and q.phone!=\"\"'> and a.phone=#{q.phone}</when> " +
            "<when test='q.handleUser!=null and q.handleUser!=\"\"'> and a.handle_user=#{q.handleUser}</when> " +
            "<when test='q.handleTime!=null and q.handleTime!=\"\"'> and a.handle_time=#{q.handleTime}</when> " +
            "<when test='q.handleAddress!=null and q.handleAddress!=\"\"'> and a.handle_address=#{q.handleAddress}</when> " +
            "<choose> " +
            "<when test='q.status!=null and q.status==1'> and a.status=1 </when>" +
            "<when test='q.status!=null and q.status==2'> and a.status!=1 and b.id is not null </when> " +
            "<when test='q.status!=null and q.status==3'> and a.status!=1 and b.id is null </when> " +
            "</choose> " +
            "</where> " +
            "order by a.add_time,a.handle_time desc " +
            "<bind name=\"keyOffset\" value=\"(q.pageNo-1)*q.pageSize\"></bind>" +
            "<if test='q.pageNo!=null and q.pageSize!=null'>limit #{keyOffset},#{q.pageSize}</if> ";

    @Select("<script>select " + QUERY_SELECT + " from " + TABLE + " " + QUERY_COND + "</script>")
    List<ApplyRecordDO> pageQuery(@Param("q") ApplyRecordPageQuery query);

    @Select("<script>select " + COUNT_SELECT + " from " + TABLE + " " + QUERY_COND + "</script>")
    Long countQuery(@Param("q") ApplyRecordPageQuery query);
}
