package com.project.ims.Requests;

import java.util.List;

import com.project.ims.Models.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareHouseAddRequest {
    private String name;

    private String address;

    private String pincode;

    private List<Product> products;

    private String manager_id;

    private String status;
}
