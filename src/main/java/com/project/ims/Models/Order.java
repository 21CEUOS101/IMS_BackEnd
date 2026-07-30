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
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @Field("product_id")
    @JsonProperty("product_id")
    private String productId;

    private String quantity;

    private String warehouseId;

    private String customerId;

    @Field("total_amount")
    @JsonProperty("total_amount")
    private String totalAmount;

    private String status;

    @Field("date_time")
    @JsonProperty("date_time")
    private String dateTime;

    @Field("payment_method")
    @JsonProperty("payment_method")
    private String paymentMethod;

    @Field("transaction_id")
    @JsonProperty("transaction_id")
    private String transactionId;

    @Field("delivery_man_id")
    @JsonProperty("delivery_man_id")
    private String deliveryManId;

    @Field("delivered_date_time")
    @JsonProperty("delivered_date_time")
    private String deliveredDateTime;

    @Field("delivery_address")
    @JsonProperty("delivery_address")
    private String deliveryAddress;

}
