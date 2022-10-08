package pers.kun.permission.model;

import lombok.Data;

/**
 * @author : qihang.liu
 * @date 2021-09-04
 */
@Data
public class Action {
    private String action;
    private String describe;
    private Boolean defaultCheck;
}
