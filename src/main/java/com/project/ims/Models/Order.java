package com.project.ims.Models;

import java.util.List;

import org.springframework.data.annotation.Id;
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

    private String product_id;

    private String quantity;

    private String warehouse_id;

    private String customerId;

    private String total_amount;

    private String status;

    private String date_time;

    private String payment_method;

    private String transaction_id;

    private String delivery_man_id;

    private String delivered_date_time;

    private String delivery_address;

}
