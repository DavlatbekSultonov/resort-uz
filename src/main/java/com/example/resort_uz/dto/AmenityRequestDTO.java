package com.example.resort_uz.dto;
import com.example.resort_uz.entity.Amenity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AmenityRequestDTO {

    @NotBlank(message = "Qulaylik nomi bo'sh bo'lmasin")
    private String name;

    private Amenity.AmenityCategory category;
    private String icon;
}