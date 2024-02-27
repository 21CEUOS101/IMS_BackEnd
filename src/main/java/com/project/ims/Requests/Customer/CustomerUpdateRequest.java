package com.project.ims.Requests.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateRequest {
    private String name;

    private String email;

    private String phone;

    private String address;

    private String pincode;
}
