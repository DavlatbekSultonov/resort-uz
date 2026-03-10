package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Rasmlar", description = "Maskan rasmlarini yuklash va boshqarish")
@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final AdminRepository adminRepository;

    @Operation(summary = "Maskanning barcha rasmlari")
    @GetMapping("/{resortId}")
    public ResponseEntity<ApiResponse> getByResort(@PathVariable Long resortId) {
        return ResponseEntity.ok(photoService.getByResort(resortId));
    }

    @Operation(summary = "Rasm yuklash", description = "Serverga saqlanadi, DB ga URL yoziladi")
    @PostMapping(value = "/admin/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> upload(
            @RequestParam Long resortId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String caption,
            @RequestParam(defaultValue = "false") Boolean isCover,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(photoService.upload(resortId, file, caption, isCover, admin.getId()));
    }

    @Operation(summary = "Rasmni asosiy qilish")
    @PatchMapping("/admin/{photoId}/cover")
    public ResponseEntity<ApiResponse> setCover(
            @PathVariable Long photoId,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(photoService.setCover(photoId, admin.getId()));
    }

    @Operation(summary = "Rasmni o'chirish", description = "Serverdan ham, DB dan ham o'chiriladi")
    @DeleteMapping("/admin/{photoId}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long photoId,
            @AuthenticationPrincipal String username) {
        Admin admin = adminRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(photoService.delete(photoId, admin.getId()));
    }
}