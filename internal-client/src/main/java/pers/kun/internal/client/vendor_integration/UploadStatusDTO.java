package pers.kun.internal.client.vendor_integration;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class UploadStatusDTO {
    private String status;
    private String message;
    private List<UploadResult> details;

    public List<UploadResult> getDetails() {
        if (null == details) {
            return Collections.emptyList();
        }
        return details;
    }

    @Data
    public static class UploadResult {
        private String skuId;
        /**
         * 是否成功
         */
        private Boolean succeed;
        /**
         * 失败时的异常消息
         */
        private String message;
    }
}
