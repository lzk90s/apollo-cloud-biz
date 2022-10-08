package pers.kun.product.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import pers.kun.core.base.BaseDO;
import pers.kun.core.base.BaseVO;
import pers.kun.core.util.SpringContextHolder;
import pers.kun.product.dao.entity.ProductSkuDO;
import pers.kun.product.service.ProductSkuDaoService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2021-09-05
 */
@Data
public class ProductVO extends BaseVO {
    /**
     * index
     */
    private Long idx;

    /**
     * 采集规则id
     */
    private Long reptileRuleId;

    /**
     * 商品所属平台
     */
    private String platform;

    /**
     * 语言
     */
    private String language;

    /**
     * 类别
     */
    private String category;

    /**
     * 主题
     */
    private String subject;

    /**
     * 描述
     */
    private String description;

    /**
     * 品牌
     */
    private String brand;

    /**
     * url
     */
    private String detailUrl;

    /**
     * 主图url
     */
    private String mainImageUrl;

    /**
     * 商品其他图片
     */
    private List<String> extraImageUrls;

    /**
     * 添加时间
     */
    private Date addTime;

    private List<SkuVO> skuList;

    @Override
    public BaseVO convertFromDO(BaseDO obj) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(obj, vo);
        var productSkuDaoService = SpringContextHolder.getBean(ProductSkuDaoService.class);
        var skuList = productSkuDaoService.list(new QueryWrapper<ProductSkuDO>()
                        .eq("goods_id", obj.getId()))
                .stream().map(k -> SkuVO.convertGoodsSkuEntity().s2t(k))
                .collect(Collectors.toList());
        vo.setSkuList(skuList);
        return vo;
    }
}
