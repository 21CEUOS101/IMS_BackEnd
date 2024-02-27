package com.project.ims.Requests.DeliveryMan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryManUpdateRequest {
    private String name;

    private String email;

    private String phone;
    
    private String warehouseId;

    private String status;
}
