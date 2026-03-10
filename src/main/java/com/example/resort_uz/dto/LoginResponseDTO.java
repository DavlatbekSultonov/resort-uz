package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Admin;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;
    private String username;
    private String fullName;
    private Admin.AdminRole role;
}
