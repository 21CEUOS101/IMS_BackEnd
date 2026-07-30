package com.project.ims.Models;

// imports
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "returnOrders")
public class ReturnOrder {

    @Id
    private String id;

    @Field("product_id")
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    private String warehouseId;

    private String customerId;

    @Field("refund_amount")
    @JsonProperty("refund_amount")
    private String refundAmount;

    @Field("return_reason")
    @JsonProperty("return_reason")
    private String returnReason;

    private String status;

    @Field("date_time")
    @JsonProperty("date_time")
    private String dateTime;

    @Field("delivery_man_id")
    @JsonProperty("delivery_man_id")
    private String deliveryManId;

    @Field("delivered_date_time")
    @JsonProperty("delivered_date_time")
    private String deliveredDateTime;

    @Field("pickup_address")
    @JsonProperty("pickup_address")
    private String pickupAddress;

    @Field("order_id")
    @JsonProperty("order_id")
    private String orderId;
    
}
