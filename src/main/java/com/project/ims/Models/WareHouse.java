package com.project.ims.Models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Field("product_ids")
    @JsonProperty("product_ids")
    private List<String> productIds;

    private List<String> quantities;

    private List<Integer> higherLimits;

    private List<Integer> lowerLimits;

    @Field("manager_id")
    @JsonProperty("manager_id")
    private String managerId;

    private String status;
    
}
