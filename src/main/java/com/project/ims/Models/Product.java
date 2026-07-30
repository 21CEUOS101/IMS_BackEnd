package com.project.ims.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Field("expiry_date")
    @JsonProperty("expiry_date")
    private String expiryDate;

    private String price;

    private String supplierId;

    private Integer tax;

    @Field("whole_sale_price")
    @JsonProperty("whole_sale_price")
    private Integer wholeSalePrice;

    private Integer profit;
    
}
