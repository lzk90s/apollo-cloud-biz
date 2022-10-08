package pers.kun.internal.client.vendor_integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EcoSkuDTO {
    private String sku;
    @JsonProperty("product_id")
    private String productId;
    private BigDecimal price;
    @JsonProperty("shipping_fee")
    private BigDecimal shippingFee;
    private Integer storage;
    @JsonProperty("style_color")
    private String styleColor;
    @JsonProperty("style_size")
    private String styleSize;
    @JsonProperty("image_url")
    private String imageUrl;
    private Float weight;
}
