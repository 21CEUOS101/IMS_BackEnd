package com.project.ims.Requests.WManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WManagerAddRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    private String warehouse_id;
}
