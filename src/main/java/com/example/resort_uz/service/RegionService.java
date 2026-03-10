package com.example.resort_uz.service;


import com.example.resort_uz.dto.RegionRequestDTO;
import com.example.resort_uz.dto.RegionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.entity.Region;
import com.example.resort_uz.repository.RegionRepository;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;
    private final com.example.resort_uz.repository.ResortRepository resortRepository;

    public ApiResponse getAll() {
        List<RegionResponseDTO> list = regionRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.builder().status(true).message("OK").data(list).build();
    }

    public ApiResponse getById(Long id) {
        Region region = regionRepository.findById(id).orElse(null);
        if (region == null) return ApiResponse.builder()
                .status(false).message("Viloyat topilmadi").build();
        return ApiResponse.builder().status(true).message("OK").data(toDTO(region)).build();
    }

    public ApiResponse create(RegionRequestDTO dto) {
        if (regionRepository.existsByCode(dto.getCode())) return ApiResponse.builder()
                .status(false).message("Bu kod allaqachon mavjud").build();

        Region region = Region.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase())
                .description(dto.getDescription())
                .centerLatitude(dto.getCenterLatitude())
                .centerLongitude(dto.getCenterLongitude())
                .build();
        regionRepository.save(region);
        return ApiResponse.builder().status(true).message("Viloyat qo'shildi").data(toDTO(region)).build();
    }

    public ApiResponse update(Long id, RegionRequestDTO dto) {
        Region region = regionRepository.findById(id).orElse(null);
        if (region == null) return ApiResponse.builder()
                .status(false).message("Viloyat topilmadi").build();

        region.setName(dto.getName());
        region.setCode(dto.getCode().toUpperCase());
        region.setDescription(dto.getDescription());
        region.setCenterLatitude(dto.getCenterLatitude());
        region.setCenterLongitude(dto.getCenterLongitude());
        regionRepository.save(region);
        return ApiResponse.builder().status(true).message("Viloyat yangilandi").data(toDTO(region)).build();
    }

    public ApiResponse delete(Long id) {
        if (!regionRepository.existsById(id)) return ApiResponse.builder()
                .status(false).message("Viloyat topilmadi").build();
        regionRepository.deleteById(id);
        return ApiResponse.builder().status(true).message("Viloyat o'chirildi").build();
    }

    private RegionResponseDTO toDTO(Region r) {
        return RegionResponseDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .code(r.getCode())
                .description(r.getDescription())
                .centerLatitude(r.getCenterLatitude())
                .centerLongitude(r.getCenterLongitude())
                .imageUrl(r.getImageUrl())
                .resortCount((int) resortRepository.countByRegionIdAndActiveTrue(r.getId()))
                .build();
    }
}