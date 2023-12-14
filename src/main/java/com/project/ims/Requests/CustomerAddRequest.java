package com.project.ims.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAddRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

    private String pincode;
}
