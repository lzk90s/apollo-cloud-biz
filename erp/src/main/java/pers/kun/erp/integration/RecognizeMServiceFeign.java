package pers.kun.erp.integration;

import org.springframework.cloud.openfeign.FeignClient;
import pers.kun.internal.client.recognize.RecognizeMService;

/**
 * @author : qihang.liu
 * @date 2021-11-25
 */
@FeignClient(name = "recognize", url = "http://recognize:33024")
//@FeignClient(name = "recognize", url = "http://localhost:33024")
public interface RecognizeMServiceFeign extends RecognizeMService {
}
