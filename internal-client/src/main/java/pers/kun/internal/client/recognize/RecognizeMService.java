package pers.kun.internal.client.recognize;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
@RequestMapping("/recognition")
public interface RecognizeMService {
    @PostMapping(value = "/excel_image_ocr_base64", consumes = "text/html; charset=utf-8")
    String excelImageOcrBase64(String base64Image);
}
