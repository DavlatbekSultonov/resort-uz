package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Resort;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResortRequestDTO {

    @NotBlank(message = "Nomi bo'sh bo'lmasin")
    private String name;

    private String shortDescription;
    private String fullDescription;

    @NotNull(message = "Maskan turini tanlang")
    private Resort.ResortType resortType;

    @NotNull(message = "Viloyatni tanlang")
    private Long regionId;

    // SUPERADMIN boshqa OWNER uchun resort yaratganda ishlatadi
    // OWNER o'zi uchun yaratganda null bo'ladi
    private Long ownerId;

    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceFromTashkent;

    private BigDecimal pricePerNightMin;
    private BigDecimal pricePerNightMax;
    private String currency;

    @NotBlank(message = "Telefon raqam bo'sh bo'lmasin")
    private String phoneNumber;

    private String phoneNumber2;
    private String email;
    private String websiteUrl;
    private String telegramLink;
    private String instagramLink;

    private Integer maxCapacity;
    private Integer roomCount;
    private Integer cottageCount;

    private Boolean active;
    private Boolean featured;
    private Boolean openYearRound;
    private Integer openMonth;
    private Integer closeMonth;

    private List<Long> amenityIds;
}