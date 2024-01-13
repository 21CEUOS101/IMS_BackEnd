package com.project.ims.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "admins")
public class Admin {

    // id will be a string starting from 'a' and random number upto 10,00,000
    @Id
    private String id;

    // name will be a string also contains spaces "Ashish Prajapati"
    private String name;

    // email will be string -> checking of email is valid or not be done at frontend
    private String email;

    // password will be a string which is encrypted by becrypt
    private String password;

    // phone number will be a 10 digits number
    private String phone;
    
}
