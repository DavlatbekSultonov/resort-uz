package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.ReviewRequestDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "8. Sharhlar", description = "Mehmon sharhlari — yozish va o'chirish")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AdminRepository adminRepository;

    @Operation(summary = "Sharh yozish", description = "Token shart emas, sharh darhol ko'rinadi")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.ok(reviewService.create(dto));
    }

    @Operation(summary = "Maskanning sharhlari")
    @GetMapping("/resort/{resortId}")
    public ResponseEntity<ApiResponse> getByResort(@PathVariable Long resortId) {
        return ResponseEntity.ok(reviewService.getByResort(resortId));
    }

    @Operation(summary = "Sharhni o'chirish", description = "OWNER faqat o'z resortining sharhini o'chiradi")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(reviewService.delete(id, admin.getId()));
    }
}