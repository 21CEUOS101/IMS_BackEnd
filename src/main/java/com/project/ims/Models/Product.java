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

    private String expiry_date;

    private String price;

    private String supplierId;

    private Integer tax;

    private Integer whole_sale_price;

    private Integer profit;
    
}
