package pers.kun.publish.model;

import lombok.Data;
import pers.kun.core.convert.BeanConverter;
import pers.kun.publish.dao.entity.PublishConfigDO;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class PublishConfigVO {
    /**
     * ID
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * 源平台
     */
    private String srcPlatform;

    /**
     * 源分类
     */
    private String srcCategory;

    /**
     * 目标平台
     */
    private String dstPlatform;

    /**
     * 目标平台账号（可以有多个，多个时使用逗号隔开）
     */
    private String dstPlatformAccount;

    /**
     * 选择器
     */
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
     * 刊登游标
     */
    private String publishCursor;

    /**
     * status
     */
    private Integer status;

    public static BeanConverter<PublishConfigDO, PublishConfigVO> convertPublishConfigEntity() {
        return BeanConverter.of(PublishConfigDO.class, PublishConfigVO.class);
    }
}
