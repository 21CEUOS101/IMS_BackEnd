package com.project.ims.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryManOutput {
    private String id;
    
    private String warehouseId;

    private String status;

    private String name;

    private String email;

    private String password;

    private String phone;
}
