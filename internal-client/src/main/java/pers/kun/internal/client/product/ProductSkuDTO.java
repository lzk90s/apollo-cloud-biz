package pers.kun.internal.client.product;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Data
public class ProductSkuDTO {
    private String gtin;
    @NotNull
    private String skuId;
    private Long goodsId;
    @NotBlank
    private String name;
    private String imageUrl;
    private Map<String, Object> skuFeature;
    @NotNull
    private BigDecimal price;
    private BigDecimal shippingFee;
    @NotBlank
    private String priceUnit;
    @NotNull
    private Integer storage;
    private String weight;
    private String followSells;

    public Map<String, Object> getSkuFeature() {
        if (null == skuFeature) {
            return Collections.emptyMap();
        }
        return skuFeature;
    }
}
