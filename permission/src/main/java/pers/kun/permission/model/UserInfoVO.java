package pers.kun.permission.model;

import lombok.Data;
import pers.kun.internal.client.uaa.UserInfoDTO;
import pers.kun.core.convert.BeanConverter;

import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
public class UserInfoVO {
    /**
     * id
     */
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 名称
     */
    private String name;

    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String phone;

    /**
     * 状态
     */
    private Boolean state;

    /**
     * 邮箱
     */
    @Pattern(regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")
    private String email;

    /**
     * 添加时间
     */
    private Date addTime;

    private String roleId;

    private RoleVO role;

    public static BeanConverter<UserInfoDTO, UserInfoVO> convertUserEntity() {
        return BeanConverter.of(UserInfoDTO.class, UserInfoVO.class);
    }
}
