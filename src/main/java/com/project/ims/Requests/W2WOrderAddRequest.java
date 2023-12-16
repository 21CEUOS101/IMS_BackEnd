package com.project.ims.Requests;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class W2WOrderAddRequest {
    private List<String> product_ids;

    private List<String> quantities;

    private String s_warehouse_id;

    private String r_warehouse_id;

    private String payment_method;

    private String transaction_id;

    private String delivery_address;
}
