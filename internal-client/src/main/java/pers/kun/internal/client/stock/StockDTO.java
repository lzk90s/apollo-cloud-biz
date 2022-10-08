package pers.kun.internal.client.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pers.kun.core.base.BaseVO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2022-01-10
 */
@Data
public class StockDTO extends BaseVO {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date ipoDate;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date outDate;
    private String industry;
    private String industryClassification;
    @NotNull
    private Boolean status;
}
