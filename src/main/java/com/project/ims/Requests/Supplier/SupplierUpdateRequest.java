package com.project.ims.Requests.Supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierUpdateRequest {
    private String name;

    private String email;

    private String phone;

    private String address;

    private String pincode;
}
