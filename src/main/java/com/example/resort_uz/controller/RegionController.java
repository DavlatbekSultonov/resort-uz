package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.RegionRequestDTO;
import com.example.resort_uz.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Viloyatlar", description = "Viloyatlarni boshqarish")
@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    // ================================================
    //  USER PANEL — ko'rish
    // ================================================

    // GET /api/regions
    @Operation(summary = "Barcha viloyatlarni ko'rish")
    @GetMapping
    public ResponseEntity<com.example.resort_uz.common.ApiResponse> getAll() {
        return ResponseEntity.ok(regionService.getAll());
    }

    // GET /api/regions/{id}
    @Operation(summary = "Bitta viloyatni ko'rish")
    @GetMapping("/{id}")
    public ResponseEntity<com.example.resort_uz.common.ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getById(id));
    }

    // ================================================
    //  ADMIN PANEL — boshqarish
    // ================================================

    // POST /api/admin/regions
    @Operation(summary = "Yangi viloyat qo'shish", description = "Faqat SUPERADMIN")
    @PostMapping("/admin")
    public ResponseEntity<com.example.resort_uz.common.ApiResponse> create(@Valid @RequestBody RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.create(dto));
    }

    // PUT /api/admin/regions/{id}
    @Operation(summary = "Viloyatni tahrirlash", description = "Faqat SUPERADMIN")
    @PutMapping("/admin/{id}")
    public ResponseEntity<com.example.resort_uz.common.ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.update(id, dto));
    }

    // DELETE /api/admin/regions/{id}
    @Operation(summary = "Viloyatni o'chirish", description = "Faqat SUPERADMIN")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.delete(id));
    }
}
