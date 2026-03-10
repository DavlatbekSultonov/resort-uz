package com.example.resort_uz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionRequestDTO {

    @NotBlank(message = "Viloyat nomi bo'sh bo'lmasin")
    private String name;

    @NotBlank(message = "Kod bo'sh bo'lmasin")
    private String code;

    private String description;
    private Double centerLatitude;
    private Double centerLongitude;
    // imageUrl rasm yuklangandan keyin alohida set qilinadi
}
