package pers.kun.internal.client.permission;

import lombok.Data;

@Data
public class PlatformAccountDTO {
    private Long id;
    private String tenantId;
    private String name;
    private String platform;
    private String platformUser;
    private String platformPassword;
    private String clientId;
    private String clientSecret;
    private String cookies;
}
