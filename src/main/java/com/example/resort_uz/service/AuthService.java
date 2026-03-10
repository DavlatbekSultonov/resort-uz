package com.example.resort_uz.service;

import com.example.resort_uz.config.JwtProvider;
import com.example.resort_uz.dto.ApiResponse;
import com.example.resort_uz.dto.LoginRequestDTO;
import com.example.resort_uz.dto.LoginResponseDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO dto) {

        // 1. Admin topildi?
        Admin admin = adminRepository.findByUsername(dto.getUsername())
                .orElse(null);

        if (admin == null) {
            return ApiResponse.error("Username yoki parol noto'g'ri");
        }

        // 2. Faolmi?
        if (!admin.getActive()) {
            return ApiResponse.error("Akkaunt bloklangan");
        }

        // 3. Parol to'g'rimi?
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            return ApiResponse.error("Username yoki parol noto'g'ri");
        }

        // 4. Token yaratish
        String token = jwtProvider.generateToken(admin.getUsername(), admin.getRole().name());

        // 5. So'nggi kirish vaqtini yangilash
        admin.setLastLoginAt(java.time.LocalDateTime.now());
        adminRepository.save(admin);

        return ApiResponse.ok("Muvaffaqiyatli kirildi",
                LoginResponseDTO.builder()
                        .token(token)
                        .username(admin.getUsername())
                        .fullName(admin.getFullName())
                        .role(admin.getRole())
                        .build()
        );
    }
}
