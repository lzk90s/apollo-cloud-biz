package pers.kun.internal.client.vendor_integration;

import org.springframework.web.bind.annotation.*;
import pers.kun.internal.client.product.ProductDTO;

import java.util.List;

@RequestMapping("/goods")
public interface EcoGoodsFacadeApi {
    @PostMapping("/upload_product/{platform}")
    String uploadProduct(@PathVariable("platform") String platform,
                         @RequestParam("clientId") String clientId,
                         @RequestParam("clientSecret") String clientSecret,
                         @RequestBody List<ProductDTO> productVOList);

    @GetMapping("/get_upload_status/{platform}")
    UploadStatusDTO getUploadStatus(@PathVariable("platform") String platform,
                                    @RequestParam("clientId") String clientId,
                                    @RequestParam("clientSecret") String clientSecret,
                                    @RequestParam("uploadId") String uploadId);

    @PostMapping("/enable_product_sale/{platform}")
    void enableProductSale(@PathVariable("platform") String platform,
                           @RequestParam("clientId") String clientId,
                           @RequestParam("clientSecret") String clientSecret,
                           @RequestBody List<String> productIdList);

    @PostMapping("/disable_product_sale/{platform}")
    void disableProductSale(@PathVariable("platform") String platform,
                            @RequestParam("clientId") String clientId,
                            @RequestParam("clientSecret") String clientSecret,
                            @RequestBody List<String> productIdList);

    @PostMapping("/delete_product/{platform}")
    void deleteProduct(@PathVariable("platform") String platform,
                       @RequestParam("clientId") String clientId,
                       @RequestParam("clientSecret") String clientSecret,
                       @RequestBody List<String> productIdList);
}
