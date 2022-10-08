package pers.kun.publish.core.item;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import pers.kun.internal.client.product.ProductDTO;
import pers.kun.publish.dao.entity.PublishConfigDO;
import pers.kun.publish.dao.entity.PublishItemDO;

@Data
@Builder
public class PublishContext {
    @JsonIgnore
    private PublishConfigDO publishConfig;
    private ProductDTO goods;
    @JsonIgnore
    private PublishItemDO publishRecord;
}