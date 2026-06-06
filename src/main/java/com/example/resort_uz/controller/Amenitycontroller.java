package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.AmenityRequestDTO;
import com.example.resort_uz.service.Amenityservice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Qulayliklar", description = "WiFi, Parking, Hovuz kabi qulayliklar")
@RestController
@RequestMapping("/amenities")
@RequiredArgsConstructor
public class Amenitycontroller {

    private final Amenityservice amenityService;

    @Operation(summary = "Barcha qulayliklarni ko'rish")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(amenityService.getAll());
    }

    @Operation(summary = "Yangi qulaylik qo'shish", description = "Faqat SUPERADMIN")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AmenityRequestDTO dto) {
        return ResponseEntity.ok(amenityService.create(dto));
    }

    @Operation(summary = "Qulaylikni tahrirlash", description = "Faqat SUPERADMIN")
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody AmenityRequestDTO dto) {
        return ResponseEntity.ok(amenityService.update(id, dto));
    }

    @Operation(summary = "Qulaylikni o'chirish", description = "Faqat SUPERADMIN")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(amenityService.delete(id));
    }
}
