package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.AdminRequestDTO;
import com.example.resort_uz.dto.AdminResponseDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Adminservice {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ApiResponse getAll() {
        List<AdminResponseDTO> list = adminRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    public ApiResponse create(AdminRequestDTO dto) {
        if (adminRepository.existsByUsername(dto.getUsername()))
            return ApiResponse.error("Bu username allaqachon band");

        Admin admin = Admin.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole() != null ? dto.getRole() : Admin.AdminRole.OWNER)
                .active(true)
                .build();
        adminRepository.save(admin);
        return ApiResponse.ok("Admin yaratildi", toDTO(admin));
    }

    public ApiResponse update(Long id, AdminRequestDTO dto) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ApiResponse.error("Admin topilmadi");

        if (dto.getFullName() != null) admin.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) admin.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));

        adminRepository.save(admin);
        return ApiResponse.ok("Admin yangilandi", toDTO(admin));
    }

    public ApiResponse delete(Long id) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ApiResponse.error("Admin topilmadi");
        if (admin.getRole() == Admin.AdminRole.SUPERADMIN)
            return ApiResponse.error("SUPERADMIN o'chirib bo'lmaydi");

        admin.setActive(false);
        adminRepository.save(admin);
        return ApiResponse.ok("Admin o'chirildi");
    }

    public ApiResponse getMe(String username) {
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin == null) return ApiResponse.error("Admin topilmadi");
        return ApiResponse.ok(toDTO(admin));
    }

    public ApiResponse updateMe(String username, AdminRequestDTO dto) {
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin == null) return ApiResponse.error("Admin topilmadi");

        if (dto.getFullName() != null) admin.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) admin.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));

        adminRepository.save(admin);
        return ApiResponse.ok("Profil yangilandi", toDTO(admin));
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
