package pers.kun.internal.client.vendor_integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RefundOrderDTO {
    private String sn;
    @JsonProperty("apply_refund_time")
    private Date applyRefundTime;
    @JsonProperty("deadline_time")
    private Date deadlineTime;
    private String sku;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("refund_reason")
    private String refundReason;
    private String remark;
}
