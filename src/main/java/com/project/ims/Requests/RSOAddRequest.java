package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RSOAddRequest {

    @JsonProperty("supplier_id")
    private String supplierId;

    @JsonProperty("refund_amount")
    private String refundAmount;

    @JsonProperty("return_reason")
    private String returnReason;

    @JsonProperty("delivery_address")
    private String deliveryAddress;

    @JsonProperty("order_id")
    private String orderId;
}
