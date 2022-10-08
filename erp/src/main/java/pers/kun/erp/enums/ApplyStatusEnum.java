package pers.kun.erp.enums;

import java.util.Arrays;

/**
 * @author : qihang.liu
 * @date 2021-11-28
 */
public enum ApplyStatusEnum {
    FAILED(1, "办理失败"),
    SUCCEED(2, "办理成功"),
    UNKNOWN(3, "未知");

    ApplyStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ApplyStatusEnum getByCode(int code) {
        return Arrays.stream(ApplyStatusEnum.values())
                .filter(s -> s.code == code).findFirst()
                .orElse(null);
    }

    public int getCode() {
        return this.code;
    }

    private int code;
    private String desc;
}
