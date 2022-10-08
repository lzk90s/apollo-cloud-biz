package pers.kun.publish.model;

import lombok.Data;
import pers.kun.core.convert.BeanConverter;
import pers.kun.publish.dao.entity.PublishConfigDO;

import javax.validation.constraints.NotBlank;

/**
 * @author : qihang.liu
 * @date 2021-06-12
 */
@Data
public class PublishConfigAddCmd {
    /**
     * name
     */
    @NotBlank
    private String name;

    /**
     * 源平台
     */
    @NotBlank
    private String srcPlatform;

    /**
     * 源分类
     */
    @NotBlank
    private String srcCategory;

    /**
     * 目标平台
     */
    @NotBlank
    private String dstPlatform;

    /**
     * 目标平台账号
     */
    @NotBlank
    private String dstPlatformAccount;

    /**
     * 选择器
     */
    @NotBlank
    private String selectors;

    /**
     * 公式
     */
    private String formulas;

    /**
     * 过滤器
     */
    private String filters;

    /**
     * 翻译器类型
     */
    private String translatorType;

    /**
     * status
     */
    private Boolean status;

    public static BeanConverter<PublishConfigAddCmd, PublishConfigDO> convertPublishConfigEntity() {
        return BeanConverter.of(PublishConfigAddCmd.class, PublishConfigDO.class);
    }
}
