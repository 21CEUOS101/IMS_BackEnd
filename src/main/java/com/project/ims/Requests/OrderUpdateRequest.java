package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    @JsonProperty("warehouse_id")
    private String warehouseId;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("total_amount")
    private String totalAmount;

    private String status;

    @JsonProperty("date_time")
    private String dateTime;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("delivery_man_id")
    private String deliveryManId;

    @JsonProperty("delivered_date_time")
    private String deliveredDateTime;

    @JsonProperty("delivery_address")
    private String deliveryAddress;
}
