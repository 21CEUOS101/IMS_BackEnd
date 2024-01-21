package com.project.ims.Models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@ToString
@Builder
public class JwtResponse {
    private String token;
    private String username;
}
