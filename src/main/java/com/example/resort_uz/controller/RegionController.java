package com.example.resort_uz.controller;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.entity.RegionEnum;
import com.example.resort_uz.repository.ResortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

    private final ResortRepository resortRepository;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<Map<String, Object>> regions = Arrays.stream(RegionEnum.values())
                .map(r -> Map.<String, Object>of(
                        "id", r.getId(),
                        "name", r.getName(),
                        "resortCount", resortRepository.countByRegionAndActiveTrue(r)
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(regions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable int id) {
        return Arrays.stream(RegionEnum.values())
                .filter(r -> r.getId() == id)
                .findFirst()
                .map(r -> ResponseEntity.ok(ApiResponse.ok(Map.<String, Object>of(
                        "id", r.getId(),
                        "name", r.getName(),
                        "resortCount", resortRepository.countByRegionAndActiveTrue(r)
                ))))
                .orElse(ResponseEntity.ok(ApiResponse.error("Viloyat topilmadi")));
    }
}
