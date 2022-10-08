package pers.kun.internal.client.product;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class ProductDTO {
    @NotBlank
    private Long id;
    @NotNull
    private Long reptileRuleId;
    @NotBlank
    private String platform;
    @NotBlank
    private String language;
    @NotBlank
    private String category;
    @NotBlank
    private String subject;
    private String description;
    private String brand;
    @NotBlank
    private String detailUrl;
    @NotBlank
    private String mainImageUrl;
    private List<String> extraImageUrls;
    private Map<String, Object> feature;
    @NotNull
    @Valid
    private List<ProductSkuDTO> skuList;

    public Map<String, Object> getFeature() {
        if (null == feature) {
            return Collections.emptyMap();
        }
        return feature;
    }

    public List<ProductSkuDTO> getSkuList() {
        if (null == skuList) {
            return Collections.emptyList();
        }
        return skuList;
    }
}
