package pers.kun.erp.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pers.kun.core.auth.UserProvider;
import pers.kun.core.exception.ItemNotExistException;
import pers.kun.core.rest.R;
import pers.kun.core.web.BaseCurdController;
import pers.kun.core.web.BaseImportController;
import pers.kun.core.web.DaoServiceImpl;
import pers.kun.erp.dao.entity.ActivateRecordDO;
import pers.kun.erp.dao.mapper.ActivateRecordMapper;
import pers.kun.erp.integration.RecognizeMServiceFeign;
import pers.kun.erp.model.*;
import pers.kun.erp.service.ActivateRecordDaoService;
import pers.kun.erp.service.HandleAddressDaoService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Slf4j
@RestController
@RequestMapping("/activate_record")
public class ActivateRecordController implements
        BaseCurdController<
                ActivateRecordMapper,
                ActivateRecordDO,
                ActivateRecordVO,
                ActivateRecordAddCmd,
                ActivateRecordUpdateCmd,
                ActivateRecordPageQuery>,
        BaseImportController<
                ActivateRecordMapper,
                ActivateRecordDO,
                ActivateRecordImportCmd> {

    @Autowired
    private RecognizeMServiceFeign recognizeApiFeign;
    @Autowired
    private HandleAddressDaoService handleAddressDaoService;
    @Autowired
    private ActivateRecordDaoService activateRecordDaoService;


    @PostMapping("/import_image")
    public R<Void> importImage(MultipartFile image) throws IOException, ParseException {
        String fileName = image.getOriginalFilename();
        assert null != fileName;
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String date, address;
        var seps = fileName.split("_");
        date = seps.length >= 1 ? seps[0] : null;
        address = seps.length >= 2 ? seps[1] : null;

        if (StringUtils.isBlank(date) || StringUtils.isBlank(address)) {
            throw new IllegalArgumentException();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = simpleDateFormat.parse(date);

        if (!handleAddressDaoService.exist(UserProvider.getTenantId(), address)) {
            throw new ItemNotExistException("地址:" + address);
        }

        var imageBytes = image.getInputStream().readAllBytes();
        var content = recognizeApiFeign.excelImageOcrBase64(Base64.getEncoder().encodeToString(imageBytes));
        if (StringUtils.isBlank(content)) {
            return R.ok();
        }

        var resultList = new ArrayList<ActivateRecordDO>();
        var lines = content.split("\n");
        for (var line : lines) {
            var columns = line.split("\t");
            if (columns.length < 2) {
                log.info("Invalid line {}", line);
                continue;
            }

            ActivateRecordDO entity = new ActivateRecordDO();
            entity.setTenantId(UserProvider.getTenantId());
            entity.setHandleAddress(address);
            entity.setHandleTime(date1);
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

    @Override
    public DaoServiceImpl<ActivateRecordMapper, ActivateRecordDO> getDaoService() {
        return activateRecordDaoService;
    }

    @Override
    public Class<ActivateRecordImportCmd> getImportClass() {
        return ActivateRecordImportCmd.class;
    }

    @Override
    public Class<ActivateRecordVO> getVoClass() {
        return ActivateRecordVO.class;
    }

    @Override
    public Class<ActivateRecordDO> getDoClass() {
        return ActivateRecordDO.class;
    }

    @Override
    public Consumer<ActivateRecordImportCmd> getBeforeImportHandler() {
        return null;
    }
}
