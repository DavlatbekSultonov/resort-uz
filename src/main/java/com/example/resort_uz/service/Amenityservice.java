package com.example.resort_uz.service;
import com.example.resort_uz.dto.AmenityRequestDTO;
import com.example.resort_uz.dto.AmenityResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.entity.Amenity;
import com.example.resort_uz.repository.AmenityRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Amenityservice {
    private final AmenityRepository amenityRepository;

    // Barcha qulayliklar
    public ApiResponse getAll() {
        List<AmenityResponseDTO> list = amenityRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ApiResponse.builder()
                .status(true)
                .message("OK")
                .data(list)
                .build();
    }

    // Yaratish (admin)
    public ApiResponse create(AmenityRequestDTO dto) {
        if (amenityRepository.existsByName(dto.getName())) {
            return ApiResponse.builder()
                    .status(false)
                    .message("Bu qulaylik allaqachon mavjud")
                    .build();
        }
        Amenity amenity = Amenity.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .icon(dto.getIcon())
                .build();
        amenityRepository.save(amenity);
        return ApiResponse.builder()
                .status(true)
                .message("Qulaylik qo'shildi")
                .data(toDTO(amenity))
                .build();
    }

    // Tahrirlash (admin)
    public ApiResponse update(Long id, AmenityRequestDTO dto) {
        Amenity amenity = amenityRepository.findById(id).orElse(null);
        if (amenity == null) return ApiResponse.builder()
                .status(false).message("Qulaylik topilmadi").build();

        amenity.setName(dto.getName());
        amenity.setCategory(dto.getCategory());
        amenity.setIcon(dto.getIcon());
        amenityRepository.save(amenity);
        return ApiResponse.builder()
                .status(true)
                .message("Qulaylik yangilandi")
                .data(toDTO(amenity))
                .build();
    }

    // O'chirish (admin)
    public ApiResponse delete(Long id) {
        if (!amenityRepository.existsById(id)) return ApiResponse.builder()
                .status(false).message("Qulaylik topilmadi").build();
        amenityRepository.deleteById(id);
        return ApiResponse.builder()
                .status(true)
                .message("Qulaylik o'chirildi")
                .build();
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
