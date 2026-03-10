package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Resort;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResortResponseDTO {


    private Long id;
    private String name;
    private String shortDescription;
    private String fullDescription;
    private Resort.ResortType resortType;

    private Long regionId;
    private String regionName;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceFromTashkent;

    private BigDecimal pricePerNightMin;
    private BigDecimal pricePerNightMax;
    private String currency;

    private String phoneNumber;
    private String phoneNumber2;
    private String email;
    private String websiteUrl;
    private String telegramLink;
    private String instagramLink;

    private Integer maxCapacity;
    private Integer roomCount;
    private Integer cottageCount;

    private Double averageRating;
    private Integer reviewCount;

    private Boolean active;
    private Boolean featured;
    private Boolean openYearRound;
    private Integer openMonth;
    private Integer closeMonth;

    private String coverImageUrl;
    private List<PhotoResponseDTO> photos;
    private List<AmenityResponseDTO> amenities;
    private List<ServiceResponseDTO> services;

    private LocalDateTime createdAt;
}