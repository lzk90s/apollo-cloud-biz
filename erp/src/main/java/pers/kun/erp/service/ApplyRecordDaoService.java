package pers.kun.erp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.erp.dao.entity.ApplyRecordDO;
import pers.kun.erp.dao.mapper.ApplyRecordMapper;
import pers.kun.erp.model.ApplyRecordPageQuery;

@Service
@Transactional
public class ApplyRecordDaoService extends DaoServiceImpl<ApplyRecordMapper, ApplyRecordDO> {

    public long pageCount(ApplyRecordPageQuery query) {
        return getBaseMapper().countQuery(query);
    }

    public Page<ApplyRecordDO> pageEx(ApplyRecordPageQuery query) {
        var total = getBaseMapper().countQuery(query);
        if (total <= 0) {
            return new Page<>(0, 0);
        }
        var list = getBaseMapper().pageQuery(query);
        var page = new Page<ApplyRecordDO>((long) (query.getPageNo() - 1) * query.getPageSize(), total);
        page.setRecords(list);
        return page;
    }
}
