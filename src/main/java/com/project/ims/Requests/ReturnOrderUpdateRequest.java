package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderUpdateRequest {

    private String return_reason;

    private String status;

    private String date_time;

    private String delivery_man_id;

    private String delivered_date_time;

    private String pickup_address;

    private String order_id;
}
