package com.project.ims.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOutput {

    private String id;

    private String address;

    private String pincode;

    private String name;

    private String email;

    private String password;

    private String phone;
    
}
