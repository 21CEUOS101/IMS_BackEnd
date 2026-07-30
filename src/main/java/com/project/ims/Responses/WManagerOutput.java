package com.project.ims.Responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WManagerOutput {
    private String id;
    private String name;
    private String email;
    private String phone;
    @JsonProperty("warehouse_id")
    private String warehouseId;
}
