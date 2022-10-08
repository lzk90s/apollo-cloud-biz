package pers.kun.internal.client.vendor_integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class OrderDTO {
    private String id;
    private String type;
    @JsonProperty("confirm_time")
    private Date confirmTime;
    private String sn;
    private String sku;
    private Integer num;
    private Float price;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("detail_url")
    private String detailUrl;
    private String remark;
}
