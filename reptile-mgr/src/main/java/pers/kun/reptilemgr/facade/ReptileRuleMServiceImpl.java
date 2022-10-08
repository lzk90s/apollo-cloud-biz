package pers.kun.reptilemgr.facade;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.auth.UserProvider;
import pers.kun.internal.client.reptile_control.ReptileRuleMService;
import pers.kun.internal.client.reptile_control.ReptileRuleDTO;
import pers.kun.reptilemgr.dao.entity.ReptileRuleDO;
import pers.kun.reptilemgr.service.ReptileRuleDaoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@RestController
public class ReptileRuleMServiceImpl implements ReptileRuleMService {
    @Autowired
    private ReptileRuleDaoService reptileRuleDaoService;

    @Override
    public List<ReptileRuleDTO> getRuleBySpiderAllTenant(@RequestParam("spider") String spider) {
        var tenants = reptileRuleDaoService.getAllTenant();
        if (CollectionUtils.isEmpty(tenants)) {
            return Collections.emptyList();
        }

        List<ReptileRuleDTO> resultList = new ArrayList<>();
        tenants.forEach(s -> {
            UserProvider.setLocalTenant(s);
            var wrapper = new QueryWrapper<ReptileRuleDO>();
            wrapper.eq("spider_name", spider);
            wrapper.eq("status", 1);
            var r = pers.kun.reptilemgr.model.ReptileRuleVO.convertReptileConfigEntity().s2t(reptileRuleDaoService.list(wrapper));
            resultList.addAll(r);
        });
        return resultList;
    }

    @Override
    public List<ReptileRuleDTO> getRuleByLabelsAllTenant(String labels) {
        var tenants = reptileRuleDaoService.getAllTenant();
        if (CollectionUtils.isEmpty(tenants)) {
            return Collections.emptyList();
        }

        var labelArray = labels.split(",");
        if (labelArray.length == 0) {
            return Collections.emptyList();
        }

        List<ReptileRuleDTO> resultList = new ArrayList<>();
        tenants.forEach(s -> {
            UserProvider.setLocalTenant(s);

            var wrapper = new QueryWrapper<ReptileRuleDO>();
            wrapper.like("labels", labelArray[0]);
            for (int i = 1; i < labelArray.length; i++) {
                wrapper.or().like("labels", labelArray[i]);
            }
            wrapper.eq("status", 1);
            var r = pers.kun.reptilemgr.model.ReptileRuleVO.convertReptileConfigEntity().s2t(reptileRuleDaoService.list(wrapper));
            resultList.addAll(r);
        });
        return resultList;
    }
}
