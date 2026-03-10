package com.example.resort_uz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "Username bo'sh bo'lmasin")
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lmasin")
    private String password;
}
