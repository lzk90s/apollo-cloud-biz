package pers.kun.internal.client.vendor_integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ScrollResult<T> {
    @JsonProperty("current_cursor")
    private String currentCursor;
    @JsonProperty("next_cursor")
    private String nextCursor;
    private List<T> results;
}
