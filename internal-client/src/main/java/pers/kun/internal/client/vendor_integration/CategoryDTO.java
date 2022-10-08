package pers.kun.internal.client.vendor_integration;

import lombok.Data;

@Data
public class CategoryDTO {
    private String name;
    private String id;
    private String url;
    private String description;
    private String vendor;
}
