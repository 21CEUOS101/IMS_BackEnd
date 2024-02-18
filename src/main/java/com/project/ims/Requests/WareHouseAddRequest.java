package com.project.ims.Requests;

// imports
import java.util.List;
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

    private List<String> product_ids;

    private List<String> quantities;

    private List<Integer> higherLimits;

    private List<Integer> lowerLimits;

    private String manager_id;

    private String status;
}
