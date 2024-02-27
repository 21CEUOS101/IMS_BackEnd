package com.project.ims.Requests.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierAddRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

    private String pincode;
}
