package pers.kun.permission.model;

import lombok.Data;
import pers.kun.permission.dao.entity.PlatformAccountDO;
import pers.kun.core.convert.BeanConverter;

/**
 * @author : qihang.liu
 * @date 2021-09-24
 */
@Data
public class PlatformAccountVO {
    /**
     * id
     */
    private Long id;

    /**
     * 名称（唯一）
     */
    private String name;

    /**
     * 平台名称
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

    public static BeanConverter<PlatformAccountDO, PlatformAccountVO> convertPlatformAccountEntity() {
        return BeanConverter.of(PlatformAccountDO.class, PlatformAccountVO.class);
    }
}
