package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAddRequest {
    private String name;

    private String expiry_date;

    private String price;

    private String supplier_id;

    private Integer tax;

    private Integer whole_sale_price;

    private Integer profit;
}
