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
@Document(collection = "returnOrders")
public class ReturnOrder {

    @Id
    private String id;

    private String product_id;

    private String quantity;

    private String warehouseId;

    private String customerId;

    private String refund_amount;

    private String return_reason;

    private String status;

    private String date_time;

    private String delivery_man_id;

    private String delivered_date_time;

    private String pickup_address;

    private String order_id;
    
}
