package com.project.ims.Models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ware_houses")
public class WareHouse {

    @Id
    private String id;

    private String name;

    private String address;

    private String pincode;

    private List<String> product_ids;

    private List<String> quantities;

    private String manager_id;

    private String status;
    
}
