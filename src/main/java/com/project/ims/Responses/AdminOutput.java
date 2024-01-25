package com.project.ims.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Admin Output Format

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminOutput {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
}
