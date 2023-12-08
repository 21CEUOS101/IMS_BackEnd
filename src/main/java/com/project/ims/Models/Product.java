package com.project.ims.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "products")
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private String category;

    private String manufactured_date;

    private String expiry_date;

    private Integer price;

    private String quantity;

    private String image;

    private String status;

    private String company_name;

    private Integer mrp;
    
}
