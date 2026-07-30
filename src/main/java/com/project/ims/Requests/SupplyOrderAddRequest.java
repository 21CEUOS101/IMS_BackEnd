package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplyOrderAddRequest {

    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    @JsonProperty("supplier_id")
    private String supplierId;

    @JsonProperty("warehouse_id")
    private String warehouseId;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("pickup_address")
    private String pickupAddress;
    
    @JsonProperty("isdelivery_man_Available")
    private boolean deliveryManAvailable;
    
}
