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
@Document(collection = "w2wOrders")
public class W2WOrder {

    @Id 
    private String id;

    @Field("product_id")
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    @Field("s_warehouse_id")
    @JsonProperty("s_warehouse_id")
    private String sWarehouseId;

    private String warehouseId;

    @Field("total_amount")
    @JsonProperty("total_amount")
    private String totalAmount;

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

    private String orderId;
    
}
