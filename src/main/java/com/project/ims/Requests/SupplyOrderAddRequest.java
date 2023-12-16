package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplyOrderAddRequest {

    private String product_id;

    private String quantity;

    private String supplier_id;

    private String warehouse_id;

    private String payment_method;

    private String transaction_id;

    private String delivery_address;
    
}
