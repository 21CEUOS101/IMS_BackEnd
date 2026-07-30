package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class W2WOrderUpdateRequest {
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    @JsonProperty("s_warehouse_id")
    private String sWarehouseId;

    @JsonProperty("r_warehouse_id")
    private String rWarehouseId;

    @JsonProperty("total_amount")
    private String totalAmount;

    private String status;

    @JsonProperty("date_time")
    private String dateTime;

    @JsonProperty("delivery_man_id")
    private String deliveryManId;

    @JsonProperty("delivered_date_time")
    private String deliveredDateTime;

    private String orderId;
}
