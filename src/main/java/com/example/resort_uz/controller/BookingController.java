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

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody BookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.create(dto));
    }

    @GetMapping("/active/{resortId}")
    public ResponseEntity<ApiResponse> getActiveBookings(@PathVariable Long resortId) {
        return ResponseEntity.ok(bookingService.getActiveBookings(resortId));
    }

    // Barcha bronlar — status filter bilan
    @Operation(summary = "Barcha bronlar", description = "status: KUTILMOQDA, TASDIQLANGAN, BEKOR_QILINGAN, YAKUNLANGAN")
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(bookingService.getAll(admin.getId(), status, pageable));
    }

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

    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(bookingService.getPending(admin.getId(), pageable));
    }

    @PatchMapping("/admin/{id}/confirm")
    public ResponseEntity<ApiResponse> confirm(
            @PathVariable Long id,
            @RequestParam(required = false) String note,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(bookingService.confirm(id, note, admin.getId()));
    }

    // PATCH /api/bookings/admin/{id}/complete
    @PatchMapping("/admin/{id}/complete")
    public ResponseEntity<ApiResponse> complete(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(bookingService.complete(id, admin.getId()));
    }

    @PatchMapping("/admin/{id}/cancel")
    public ResponseEntity<ApiResponse> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(bookingService.cancel(id, reason, admin.getId()));
    }
}
