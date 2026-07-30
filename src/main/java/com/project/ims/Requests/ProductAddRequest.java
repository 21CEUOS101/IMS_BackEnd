package com.project.ims.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAddRequest {
    private String name;

    @JsonProperty("expiry_date")
    private String expiryDate;

    private String price;

    private String supplierId;

    private Integer tax;

    @JsonProperty("whole_sale_price")
    private Integer wholeSalePrice;

    private Integer profit;
}
