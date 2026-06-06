package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.AmenityRequestDTO;
import com.example.resort_uz.dto.AmenityResponseDTO;
import com.example.resort_uz.entity.Amenity;
import com.example.resort_uz.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Amenityservice {

    private final AmenityRepository amenityRepository;

    public ApiResponse getAll() {
        List<AmenityResponseDTO> list = amenityRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    public ApiResponse create(AmenityRequestDTO dto) {
        if (amenityRepository.existsByName(dto.getName()))
            return ApiResponse.error("Bu qulaylik allaqachon mavjud");

        Amenity amenity = Amenity.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .icon(dto.getIcon())
                .build();
        amenityRepository.save(amenity);
        return ApiResponse.ok("Qulaylik qo'shildi", toDTO(amenity));
    }

    public ApiResponse update(Long id, AmenityRequestDTO dto) {
        Amenity amenity = amenityRepository.findById(id).orElse(null);
        if (amenity == null) return ApiResponse.error("Qulaylik topilmadi");

        amenity.setName(dto.getName());
        amenity.setCategory(dto.getCategory());
        amenity.setIcon(dto.getIcon());
        amenityRepository.save(amenity);
        return ApiResponse.ok("Qulaylik yangilandi", toDTO(amenity));
    }

    public ApiResponse delete(Long id) {
        if (!amenityRepository.existsById(id))
            return ApiResponse.error("Qulaylik topilmadi");
        amenityRepository.deleteById(id);
        return ApiResponse.ok("Qulaylik o'chirildi");
    }

    private AmenityResponseDTO toDTO(Amenity a) {
        return AmenityResponseDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .category(a.getCategory())
                .icon(a.getIcon())
                .build();
    }
}
