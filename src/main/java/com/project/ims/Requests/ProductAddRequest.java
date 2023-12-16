package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAddRequest {
    private String name;

    private String description;

    private String category;

    private String manufactured_date;

    private String expiry_date;

    private String price;

    private String image;

    private String company_name;

    private String mrp;
}
