package com.project.ims.Requests.WManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WManagerUpdateRequest {
    private String name;

    private String email;

    private String phone;

    private String warehouseId;
}
