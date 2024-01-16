package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderAddRequest {
    private String order_id;

    private String customer_id;

    private String return_reason;

    private String pickup_address;
}
