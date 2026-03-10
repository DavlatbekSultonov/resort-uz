package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.ResortRequestDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.service.ResortService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Tag(name = "Maskanlar", description = "Dam olish maskanlarini boshqarish")
@RestController
@RequestMapping("/resorts")
@RequiredArgsConstructor
public class ResortController {

    private final ResortService resortService;
    private final AdminRepository adminRepository;

    // ================================================
    //  USER PANEL
    // ================================================

    @Operation(summary = "Barcha faol maskanlar", description = "Sahifalash bilan (page, size)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(resortService.getAll(pageable));
    }

    @Operation(summary = "Maskanlarni filtrlash",
            description = "Viloyat, tur, narx, qidiruv, sana va joylashuv bo'yicha. " +
                    "checkIn/checkOut — bo'sh maskanlar. userLat/userLon — masofani hisoblash")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filter(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String resortType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) Double userLat,
            @RequestParam(required = false) Double userLon,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(resortService.filter(
                regionId, resortType, minPrice, maxPrice, search,
                checkIn, checkOut, userLat, userLon, pageable));
    }

    @Operation(summary = "Tavsiya etilgan maskanlar", description = "Bosh sahifa uchun featured maskanlar")
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse> getFeatured() {
        return ResponseEntity.ok(resortService.getFeatured());
    }

    @Operation(summary = "Bitta maskan — to'liq ma'lumot")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resortService.getById(id));
    }

    // ================================================
    //  ADMIN PANEL
    // ================================================

    @Operation(summary = "Admin o'z maskanlarini ko'radi", description = "Token orqali admin aniqlanadi")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse> getByAdmin(
            @AuthenticationPrincipal String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(resortService.getByAdmin(admin.getId(), pageable));
    }

    @Operation(summary = "Yangi maskan qo'shish",
            description = "SUPERADMIN ownerId yuborsa — o'sha OWNER ga birikadi")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody ResortRequestDTO dto,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(resortService.create(dto, admin.getId()));
    }

    @Operation(summary = "Maskanni tahrirlash", description = "OWNER faqat o'z maskanini, SUPERADMIN hammasini")
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ResortRequestDTO dto,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(resortService.update(id, dto, admin.getId()));
    }

    @Operation(summary = "Maskanni o'chirish")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(resortService.delete(id, admin.getId()));
    }

    @Operation(summary = "Maskanni faollashtirish / o'chirish")
    @PatchMapping("/admin/{id}/toggle")
    public ResponseEntity<ApiResponse> toggleActive(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(resortService.toggleActive(id));
    }
}