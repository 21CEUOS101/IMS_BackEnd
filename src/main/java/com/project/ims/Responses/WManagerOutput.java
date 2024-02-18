package com.project.ims.Responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WManagerOutput {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String warehouse_id;
}
