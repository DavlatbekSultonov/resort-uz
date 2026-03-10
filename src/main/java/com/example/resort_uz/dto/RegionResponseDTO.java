package com.example.resort_uz.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionResponseDTO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Double centerLatitude;
    private Double centerLongitude;
    private String imageUrl;
    private Integer resortCount;
}
