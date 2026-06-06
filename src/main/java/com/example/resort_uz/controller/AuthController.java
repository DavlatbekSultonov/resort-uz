package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.LoginRequestDTO;
import com.example.resort_uz.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Autentifikatsiya", description = "Tizimga kirish")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Tizimga kirish", description = "Username va parol bilan kirish, JWT token qaytaradi")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
