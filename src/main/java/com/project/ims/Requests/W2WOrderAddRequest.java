package com.project.ims.Requests;

// imports
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class W2WOrderAddRequest {
    private String product_id;

    private String quantity;

    private String s_warehouse_id;

    private String r_warehouse_id;

    private String orderId;
}
