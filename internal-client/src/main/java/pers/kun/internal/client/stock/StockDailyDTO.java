package pers.kun.internal.client.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author : qihang.liu
 * @date 2022-01-07
 */
@Data
public class StockDailyDTO {
    @NotBlank
    private String code;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    @NotNull
    private Float open;
    @NotNull
    private Float high;
    @NotNull
    private Float low;
    @NotNull
    private Float close;
    @NotNull
    private Float preClose;
    @NotNull
    private Float change;
    @NotNull
    private Float changePercent;
    @NotNull
    private Long volume;
    @NotNull
    private Long amount;
    @NotNull
    private Float turn;
    private Boolean traceStatus;
    private Float peTtm;
    private Double tcap;
    private Double mcap;
}
