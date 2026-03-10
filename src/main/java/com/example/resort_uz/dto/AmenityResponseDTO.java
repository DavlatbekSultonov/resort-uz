package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Amenity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityResponseDTO {

    private Long id;
    private String name;
    private Amenity.AmenityCategory category;
    private String icon;
}
