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

    private List<String> product_ids;

    private List<String> quantities;

    private String source_id;

    private String destination_id;

    private String refund_amount;

    private String return_reason;

    private String status;

    private String date_time;

    private String delivery_man_id;

    private String delivered_date_time;

    private String delivery_address;
    
}
