package com.example.resort_uz.controller;
import com.example.resort_uz.dto.AdminRequestDTO;
import com.example.resort_uz.service.Adminservice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.resort_uz.common.ApiResponse;
@Tag(name = "Adminlar", description = "Admin boshqaruvi")
@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class Admincontroller {

    private final Adminservice adminService;

    // ================================================
    //  SUPERADMIN endpointlari
    // ================================================

    // Barcha adminlarni ko'rish
    @Operation(summary = "Barcha adminlarni ko'rish", description = "Faqat SUPERADMIN uchun")
    @GetMapping("/superadmin")
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(adminService.getAll());
    }

    // Yangi OWNER yaratish
    @Operation(summary = "Yangi maskan egasi (OWNER) yaratish", description = "Faqat SUPERADMIN yarata oladi")
    @PostMapping("/superadmin")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AdminRequestDTO dto) {
        return ResponseEntity.ok(adminService.create(dto));
    }

    // Adminni tahrirlash
    @Operation(summary = "Adminni tahrirlash", description = "Faqat SUPERADMIN tahrirlaydi")
    @PutMapping("/superadmin/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminRequestDTO dto) {
        return ResponseEntity.ok(adminService.update(id, dto));
    }

    // Adminni o'chirish (deaktivatsiya)
    @Operation(summary = "Adminni o'chirish (deaktivatsiya)", description = "Owner o'chira olmaydi")
    @DeleteMapping("/superadmin/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.delete(id));
    }

    // ================================================
    //  O'Z PROFILI (OWNER ham, SUPERADMIN ham)
    // ================================================

    // O'z profilini ko'rish
    @Operation(summary = "O'z profilini ko'rish", description = "SUPERADMIN va OWNER uchun")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMe(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(adminService.getMe(username));
    }

    // O'z profilini yangilash (faqat ism, tel, parol)
    @Operation(summary = "O'z profilini yangilash", description = "Faqat ism, telefon va parolni o'zgartirish mumkin")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse> updateMe(
            @AuthenticationPrincipal String username,
            @RequestBody AdminRequestDTO dto) {
        return ResponseEntity.ok(adminService.updateMe(username, dto));
    }
}
