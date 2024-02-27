package com.project.ims.Requests.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateRequest {
    
    private String name;

    private String email;

    private String phone;
}
