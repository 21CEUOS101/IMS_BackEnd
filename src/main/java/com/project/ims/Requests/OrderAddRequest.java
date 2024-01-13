package com.project.ims.Requests;

import java.util.List;

import com.project.ims.Models.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddRequest {

    private List<String> product_ids;

    private List<String> quantities;

    private String warehouse_id;

    private String customer_id;

    private String payment_method;

    private String transaction_id;

    private String delivery_address;
}
