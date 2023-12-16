package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RSOAddRequest {

    private String supplier_id;

    private String refund_amount;

    private String return_reason;

    private String delivery_address;

    private String order_id;
}
