package com.project.ims.Models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@ToString
@Builder
public class JwtRequest {
    private String username;
    private String password;
}
