package com.project.ims.Requests;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddRequest {

    private List<String> product_ids;

    private String source_id;

    private String destination_id;

    private String total_amount;

    private String date_time;

    private String payment_method;

    private String transaction_id;

    private String delivery_address;
}
