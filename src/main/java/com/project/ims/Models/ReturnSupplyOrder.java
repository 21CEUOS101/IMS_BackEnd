package com.project.ims.Models;

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
@Document(collection = "returnSupplyOrders")
public class ReturnSupplyOrder {
    @Id
    private String id;

    @Field("product_id")
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    @Field("warehouse_id")
    @JsonProperty("warehouse_id")
    private String warehouseId;

    private String supplierId;

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

    @Field("delivery_address")
    @JsonProperty("delivery_address")
    private String deliveryAddress;

    @Field("order_id")
    @JsonProperty("order_id")
    private String orderId;
}
