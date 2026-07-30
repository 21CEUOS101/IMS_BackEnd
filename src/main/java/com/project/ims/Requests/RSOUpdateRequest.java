package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RSOUpdateRequest {
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    @JsonProperty("warehouse_id")
    private String warehouseId;

    @JsonProperty("supplier_id")
    private String supplierId;

    @JsonProperty("refund_amount")
    private String refundAmount;

    @JsonProperty("return_reason")
    private String returnReason;

    private String status;

    @JsonProperty("date_time")
    private String dateTime;

    @JsonProperty("delivery_man_id")
    private String deliveryManId;

    @JsonProperty("delivered_date_time")
    private String deliveredDateTime;

    @JsonProperty("delivery_address")
    private String deliveryAddress;

    @JsonProperty("order_id")
    private String orderId;
}
