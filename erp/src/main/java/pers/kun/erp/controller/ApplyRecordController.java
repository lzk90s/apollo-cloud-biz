package pers.kun.erp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pers.kun.core.auth.UserConst;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.convert.BeanConverter;
import pers.kun.core.exception.ItemNotExistException;
import pers.kun.core.rest.R;
import pers.kun.core.util.JsonUtil;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.core.web.model.PageData;
import pers.kun.erp.dao.entity.ApplyRecordDO;
import pers.kun.erp.dao.mapper.ApplyRecordMapper;
import pers.kun.erp.enums.ApplyStatusEnum;
import pers.kun.erp.integration.RecognizeMServiceFeign;
import pers.kun.erp.model.*;
import pers.kun.erp.service.ApplyRecordDaoService;
import pers.kun.erp.service.HandleAddressDaoService;
import pers.kun.erp.service.HandleUserDaoService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Slf4j
@RestController
@RequestMapping("/apply_record")
public class ApplyRecordController implements
        BaseCurdController<
                ApplyRecordMapper,
                ApplyRecordDO,
                ApplyRecordVO,
                ApplyRecordAddCmd,
                ApplyRecordUpdateCmd,
                ApplyRecordPageQuery> {

    @Autowired
    private RecognizeMServiceFeign recognizeApiFeign;
    @Autowired
    private HandleUserDaoService handleUserDaoService;
    @Autowired
    private HandleAddressDaoService handleAddressDaoService;
    @Autowired
    private ApplyRecordDaoService applyRecordDaoService;

    @GetMapping("/statistic/user_apply_num")
    public R<List<StatUserApplyNumVO>> statisticUserApplyNum() {
        var wrapper = new QueryWrapper<ApplyRecordDO>()
                .select("handle_user as handleUser, count(*) as num")
                .eq(UserConst.TENANT_ID, UserProvider.getTenantId())
                .groupBy("handle_user");
        var result = getDaoService().listMaps(wrapper);
        if (CollectionUtils.isEmpty(result)) {
            return R.ok();
        }
        var list = result.stream()
                .map(s -> JsonUtil.json2pojo(JsonUtil.obj2json(s), StatUserApplyNumVO.class))
                .collect(Collectors.toList());
        return R.ok(list);
    }

    @Override
    public DaoServiceImpl<ApplyRecordMapper, ApplyRecordDO> getDaoService() {
        return null;
    }

    @Override
    public Class<ApplyRecordVO> getVoClass() {
        return null;
    }

    @Override
    public Class<ApplyRecordDO> getDoClass() {
        return null;
    }

    @Override
    public R<PageData<ApplyRecordVO>> getPage(ApplyRecordPageQuery query) {
        query.setTenantId(UserProvider.getTenantId());
        var page = ((ApplyRecordDaoService) getDaoService()).pageEx(query);
        if (page.getRecords() == null) {
            return R.ok(new PageData<>(0L, 0L, null));
        }
        var list = page.getRecords().stream()
                .map(s -> BeanConverter.of(ApplyRecordDO.class, ApplyRecordVO.class).s2t(s))
                .collect(Collectors.toList());
        return R.ok(new PageData<>(page.getCurrent(), page.getTotal(), list));
    }

    @GetMapping("/statistic/apply_status")
    public R<List<StatApplyStatusVO>> statisticApplyStatus() {
        Function<Integer, Long> queryFun = (status) -> {
            var query = new ApplyRecordPageQuery();
            query.setTenantId(UserProvider.getTenantId());
            query.setStatus(status);
            return ((ApplyRecordDaoService) getDaoService()).pageCount(query);
        };
        var result = Arrays.asList(
                new StatApplyStatusVO(ApplyStatusEnum.FAILED.getCode(),
                        queryFun.apply(ApplyStatusEnum.FAILED.getCode())),
                new StatApplyStatusVO(ApplyStatusEnum.SUCCEED.getCode(),
                        queryFun.apply(ApplyStatusEnum.SUCCEED.getCode())),
                new StatApplyStatusVO(ApplyStatusEnum.UNKNOWN.getCode(),
                        queryFun.apply(ApplyStatusEnum.UNKNOWN.getCode()))
        );
        return R.ok(result);
    }

    @PostMapping("/import_image")
    public R<Void> importImage(MultipartFile image) throws IOException, ParseException {
        String fileName = image.getOriginalFilename();
        assert null != fileName;
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String date, address, user;
        var seps = fileName.split("_");
        date = seps.length >= 1 ? seps[0] : null;
        address = seps.length >= 2 ? seps[1] : null;
        user = seps.length >= 3 ? seps[2] : null;

        if (StringUtils.isBlank(date) || StringUtils.isBlank(address) || StringUtils.isBlank(user)) {
            throw new IllegalArgumentException();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = simpleDateFormat.parse(date);

        if (!handleUserDaoService.exist(UserProvider.getTenantId(), user)) {
            throw new ItemNotExistException("经办人:" + user);
        }
        if (!handleAddressDaoService.exist(UserProvider.getTenantId(), address)) {
            throw new ItemNotExistException("地址:" + address);
        }

        var imageBytes = image.getBytes();
        var content = recognizeApiFeign.excelImageOcrBase64(Base64.getEncoder().encodeToString(imageBytes));
        if (StringUtils.isBlank(content)) {
            return R.ok();
        }

        var resultList = new ArrayList<ApplyRecordDO>();
        var lines = content.split("\n");
        for (var line : lines) {
            var columns = line.split("\t");
            if (columns.length < 2) {
                log.info("Invalid line {}", line);
                continue;
            }

            ApplyRecordDO entity = new ApplyRecordDO();
            entity.setTenantId(UserProvider.getTenantId());
            entity.setHandleUser(user);
            entity.setHandleTime(date1);
            entity.setHandleAddress(address);
            for (int i = 0; i < columns.length; i++) {
                if (i == 1) {
                    entity.setUser(columns[i]);
                } else if (i == 2) {
                    entity.setPhone(columns[i]);
                }
            }

            resultList.add(entity);
        }

        getDaoService().saveOrUpdateBatch(resultList);

        return R.ok();
    }
}
