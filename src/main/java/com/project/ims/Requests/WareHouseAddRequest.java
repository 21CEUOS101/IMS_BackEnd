package com.project.ims.Requests;

// imports
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareHouseAddRequest {
    private String name;

    private String address;

    private String pincode;

    @JsonProperty("product_ids")
    private List<String> productIds;

    private List<String> quantities;

    private List<Integer> higherLimits;

    private List<Integer> lowerLimits;

    @JsonProperty("manager_id")
    private String managerId;

    private String status;
}
