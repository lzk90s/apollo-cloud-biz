package pers.kun.internal.client.product;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/internal/product")
public interface ProductMService {
    @PostMapping
    void saveProduct(@RequestBody ProductDTO productVO);

    @GetMapping("/{productId}")
    ProductDTO getProduct(@PathVariable("productId") Long productId);

    @GetMapping("/sku/{skuId}")
    ProductSkuDTO getSku(@PathVariable("skuId") String skuId);

    @GetMapping("/{productId}/exists")
    boolean isProductExists(@PathVariable("productId") String productId);

    @PostMapping("/walk")
    ProductWalkResult walkProduct(@RequestBody ProductWalkOption option);

    @PostMapping("/walk_sku")
    SkuWalkResult walkSku(@RequestBody SkuWalkOption option);
}
