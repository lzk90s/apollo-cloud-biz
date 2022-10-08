package pers.kun.permission.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.kun.core.web.model.BaseTenantDO;

@Data
@TableName("t_platform_account")
public class PlatformAccountDO extends BaseTenantDO {
    /**
     * 名称（唯一）
     */
    private String name;

    /**
     * 平台
     */
    private String platform;

    /**
     * 平台用户
     */
    private String platformUser;

    /**
     * 平台密码
     */
    private String platformPassword;

    /**
     * client id
     */
    private String clientId;

    /**
     * client secret
     */
    private String clientSecret;

    /**
     * cookies
     */
    private String cookies;
}
