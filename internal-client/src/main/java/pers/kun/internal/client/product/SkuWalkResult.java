package pers.kun.internal.client.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : qihang.liu
 * @date 2021-08-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuWalkResult {
    private String cursor;
    private List<ProductSkuDTO> goodsList;
}
