package com.project.ims.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "delivery_man")
public class DeliveryMan {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private String phone;
    
}
