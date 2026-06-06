package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.config.JwtProvider;
import com.example.resort_uz.dto.LoginRequestDTO;
import com.example.resort_uz.dto.LoginResponseDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public ApiResponse login(LoginRequestDTO dto) {
        Admin admin = adminRepository.findByUsername(dto.getUsername()).orElse(null);
        if (admin == null)
            return ApiResponse.error("Username yoki parol noto'g'ri");
        if (!admin.getActive())
            return ApiResponse.error("Akkaunt bloklangan");
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword()))
            return ApiResponse.error("Username yoki parol noto'g'ri");

        String token = jwtProvider.generateToken(admin.getUsername(), admin.getRole().name());
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);

        return ApiResponse.ok("Muvaffaqiyatli kirildi",
                LoginResponseDTO.builder()
                        .token(token)
                        .username(admin.getUsername())
                        .fullName(admin.getFullName())
                        .role(admin.getRole())
                        .build());
    }
}
