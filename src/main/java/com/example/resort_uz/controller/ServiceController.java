package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.ServiceRequestDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "7. Xizmatlar", description = "Hovuz, Sauna, Restoran kabi xizmatlar")
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;
    private final AdminRepository adminRepository;

    @Operation(summary = "Maskanning barcha xizmatlari")
    @GetMapping("/{resortId}")
    public ResponseEntity<ApiResponse> getByResort(@PathVariable Long resortId) {
        return ResponseEntity.ok(serviceService.getByResort(resortId));
    }

    @Operation(summary = "Yangi xizmat qo'shish", description = "SUPERADMIN va OWNER")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody ServiceRequestDTO dto,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(serviceService.create(dto, admin.getId()));
    }

    @Operation(summary = "Xizmatni tahrirlash")
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDTO dto,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(serviceService.update(id, dto, admin.getId()));
    }

    @Operation(summary = "Xizmatni o'chirish")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(serviceService.delete(id, admin.getId()));
    }
}