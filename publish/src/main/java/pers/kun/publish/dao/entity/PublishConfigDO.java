package pers.kun.publish.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

/**
 * 产品刊登配置
 *
 * @author : qihang.liu
 * @date 2021-05-15
 */
@Data
@TableName(value = "t_publish_config", autoResultMap = true)
public class PublishConfigDO extends BaseTenantDO {
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
    private Long publishCursor;

    /**
     * status
     */
    private Integer status;
}
