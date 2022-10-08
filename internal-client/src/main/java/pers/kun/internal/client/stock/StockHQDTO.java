package pers.kun.internal.client.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2022-01-06
 */
@Data
public class StockHQDTO {
    @NotBlank
    private String code;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date hqTime;
    @NotNull
    private Float price;
    @NotNull
    private Float change;
    @NotNull
    private Float changePercent;
}
