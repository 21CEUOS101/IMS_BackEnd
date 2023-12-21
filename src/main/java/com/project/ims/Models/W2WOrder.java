package com.project.ims.Models;

import org.springframework.data.annotation.Id;
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

    private String product_id;

    private String quantity;

    private String s_warehouse_id;

    private String r_warehouse_id;

    private String total_amount;

    private String status;

    private String date_time;

    private String delivery_man_id;

    private String delivered_date_time;
    
}
