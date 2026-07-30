package com.project.ims.Requests.WManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WManagerAddRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    @JsonProperty("warehouse_id")
    private String warehouseId;
}
