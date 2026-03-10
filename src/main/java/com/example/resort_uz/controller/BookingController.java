package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.BookingRequestDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "9. Band qilish", description = "Maskan band qilish va boshqarish")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final AdminRepository adminRepository;

    // POST /api/bookings — mehmon band qiladi (token shart emas)
    @Operation(summary = "Maskan band qilish",
            description = "Token shart emas. Yuborilganda adminga va mehmoniga SMS keladi")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody BookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.create(dto));
    }

    // GET /api/bookings/active/{resortId} — band bo'lgan sanalar (calendar uchun)
    @Operation(summary = "Band bo'lgan sanalar",
            description = "Frontend calendar uchun — qaysi kunlar band ekanligini ko'rsatadi")
    @GetMapping("/active/{resortId}")
    public ResponseEntity<ApiResponse> getActiveBookings(@PathVariable Long resortId) {
        return ResponseEntity.ok(bookingService.getActiveBookings(resortId));
    }

    // ================================================
    //  ADMIN PANEL
    // ================================================

    // GET /api/bookings/admin/resort/{resortId}
    @Operation(summary = "Maskanning barcha band qilishlari", description = "OWNER faqat o'ziniki ko'radi")
    @GetMapping("/admin/resort/{resortId}")
    public ResponseEntity<ApiResponse> getByResort(
            @PathVariable Long resortId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(bookingService.getByResort(resortId, admin.getId(), pageable));
    }

    // GET /api/bookings/admin/pending
    @Operation(summary = "Kutilayotgan band qilishlar",
            description = "SUPERADMIN — barcha, OWNER — faqat o'ziniki")
    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(bookingService.getPending(admin.getId(), pageable));
    }

    // PATCH /api/bookings/admin/{id}/confirm
    @Operation(summary = "Band qilishni tasdiqlash",
            description = "note — ixtiyoriy izoh, mehmoniga SMS ga boradi")
    @PatchMapping("/admin/{id}/confirm")
    public ResponseEntity<ApiResponse> confirm(
            @PathVariable Long id,
            @RequestParam(required = false) String note,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(bookingService.confirm(id, note, admin.getId()));
    }

    // PATCH /api/bookings/admin/{id}/cancel
    @Operation(summary = "Band qilishni bekor qilish",
            description = "reason — bekor qilish sababi, mehmoniga SMS ga boradi")
    @PatchMapping("/admin/{id}/cancel")
    public ResponseEntity<ApiResponse> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(bookingService.cancel(id, reason, admin.getId()));
    }
}