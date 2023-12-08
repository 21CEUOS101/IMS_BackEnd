package com.project.ims.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "customers")
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    
    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

    private String pincode;

}
