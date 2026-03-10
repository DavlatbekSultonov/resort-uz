package com.example.resort_uz.service;
import com.example.resort_uz.dto.AdminRequestDTO;
import com.example.resort_uz.dto.AdminResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Adminservice {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // ================================================
    //  SUPERADMIN — barcha adminlarni ko'radi
    // ================================================

    public ApiResponse getAll() {
        List<AdminResponseDTO> list = adminRepository.findAll()
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
        return ApiResponse.builder().status(true).message("OK").data(list).build();
    }

    // SUPERADMIN — yangi OWNER yaratadi
    public ApiResponse create(AdminRequestDTO dto) {
        if (adminRepository.existsByUsername(dto.getUsername())) {
            return ApiResponse.builder()
                    .status(false)
                    .message("Bu username allaqachon band")
                    .build();
        }
        Admin admin = Admin.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .role(Admin.AdminRole.OWNER)
                .active(true)
                .build();
        adminRepository.save(admin);
        return ApiResponse.builder()
                .status(true)
                .message("Admin yaratildi")
                .data(toDTO(admin))
                .build();
    }

    // SUPERADMIN — adminni tahrirlash
    public ApiResponse update(Long id, AdminRequestDTO dto) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();

        admin.setFullName(dto.getFullName());
        admin.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        adminRepository.save(admin);
        return ApiResponse.builder()
                .status(true).message("Admin yangilandi").data(toDTO(admin)).build();
    }

    // SUPERADMIN — adminni o'chirish
    public ApiResponse delete(Long id) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();
        if (admin.getRole() == Admin.AdminRole.SUPERADMIN) {
            return ApiResponse.builder()
                    .status(false).message("SUPERADMIN o'chirib bo'lmaydi").build();
        }
        admin.setActive(false);
        adminRepository.save(admin);
        return ApiResponse.builder()
                .status(true).message("Admin o'chirildi").build();
    }

    // ================================================
    //  OWNER — o'z profilini boshqaradi
    // ================================================

    public ApiResponse getMe(String username) {
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();
        return ApiResponse.builder()
                .status(true).message("OK").data(toDTO(admin)).build();
    }

    public ApiResponse updateMe(String username, AdminRequestDTO dto) {
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();

        // Faqat ism, telefon va parol o'zgartirish mumkin
        if (dto.getFullName() != null) admin.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) admin.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        adminRepository.save(admin);
        return ApiResponse.builder()
                .status(true).message("Profil yangilandi").data(toDTO(admin)).build();
    }

    private AdminResponseDTO toDTO(Admin a) {
        return AdminResponseDTO.builder()
                .id(a.getId())
                .username(a.getUsername())
                .fullName(a.getFullName())
                .phoneNumber(a.getPhoneNumber())
                .role(a.getRole())
                .active(a.getActive())
                .createdAt(a.getCreatedAt())
                .lastLoginAt(a.getLastLoginAt())
                .build();
    }
}
