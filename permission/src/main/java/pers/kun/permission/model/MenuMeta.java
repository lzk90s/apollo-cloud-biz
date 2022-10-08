package pers.kun.permission.model;

import lombok.Data;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
public class MenuMeta {
    private String icon;
    private String title;
    private Boolean hide;
    private Boolean requiresAuth = true;
    private List<String> permissions;
    private String singleLayout;
    private Integer order;
}
