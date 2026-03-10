package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Admin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRequestDTO {

    @NotBlank(message = "Username bo'sh bo'lmasin")
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lmasin")
    private String password;

    @NotBlank(message = "To'liq ism bo'sh bo'lmasin")
    private String fullName;

    private String phoneNumber;
    private Admin.AdminRole role;
}
