package pers.kun.erp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : qihang.liu
 * @date 2021-11-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatUserApplyNumVO {
    private String handleUser;
    private Long num;
}
