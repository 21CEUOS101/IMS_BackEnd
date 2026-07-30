package com.project.ims.Requests;

// imports
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class W2WOrderAddRequest {
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    @JsonProperty("s_warehouse_id")
    private String sWarehouseId;

    @JsonProperty("r_warehouse_id")
    private String rWarehouseId;

    private String orderId;
}
