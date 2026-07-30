package com.project.ims.Requests;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddRequest {

    @JsonProperty("product_ids")
    private List<String> productIds;

    private List<String> quantities;

    @JsonProperty("warehouse_id")
    private String warehouseId;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("delivery_address")
    private String deliveryAddress;
}
