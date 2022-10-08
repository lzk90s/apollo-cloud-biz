package pers.kun.publish.core;

import java.util.Arrays;

public enum PublishStatusEnum {
    /**
     * 已初始化
     */
    INITIALIZED(0),
    /**
     * 上传中
     */
    UPLOADING(1),
    /**
     * 已上传完成
     */
    UPLOADED(2),
    /**
     * 已忽略(已经忽略的不会上架）
     */
    IGNORED(3),
    /**
     * 已经确认（已经确认的会上架）
     */
    CONFIRMED(4),
    /**
     * 已上架
     */
    ON_SALE(5),
    /**
     * 已下架
     */
    DISABLE_SALE(6),
    /**
     * 下架中
     */
    WAIT_DISABLE_SALE(7),

    /**
     * 失败
     */
    FAILED(99);

    private final int status;

    PublishStatusEnum(int status) {
        this.status = status;
    }

    public static PublishStatusEnum getByStatus(int status) {
        return Arrays.stream(PublishStatusEnum.values())
                .filter(s -> s.getStatus() == status)
                .findFirst()
                .orElse(null);
    }

    public int getStatus() {
        return this.status;
    }
}
