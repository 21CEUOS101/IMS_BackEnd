package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplyOrderUpdateRequest {
    private String product_id;

    private String quantity;

    private String warehouse_id;

    private String supplier_id;

    private String total_amount;

    private String status;

    private String date_time;

    private String payment_method;

    private String transaction_id;

    private String delivery_man_id;

    private String delivered_date_time;

    private String pickup_address;
}
