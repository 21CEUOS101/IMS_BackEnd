package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class W2WOrderUpdateRequest {
    private String product_id;

    private String quantity;

    private String s_warehouse_id;

    private String r_warehouse_id;

    private String total_amount;

    private String status;

    private String date_time;

    private String delivery_man_id;

    private String delivered_date_time;

    private String orderId;
}
