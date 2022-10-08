package pers.kun.publish.core.item;

/**
 * 商品item生成器
 *
 * @author : qihang.liu
 * @date 2021-09-06
 */
public interface ItemGenerator {
    String GENERATOR = "ItemGenerator";

    void generate(PublishContext context);
}
