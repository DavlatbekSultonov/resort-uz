package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Admin;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResponseDTO {

    private Long id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private Admin.AdminRole role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
