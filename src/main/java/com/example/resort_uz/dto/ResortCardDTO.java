package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Resort;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResortCardDTO {

    private Long id;
    private String name;
    private Resort.ResortType resortType;
    private String regionName;
    private String address;
    private BigDecimal pricePerNightMin;
    private String currency;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean featured;
    private String coverImageUrl;
    private boolean active;
    // Foydalanuvchidan masofasi (km) — userLat/userLon yuborilsa hisoblanadi
    private Double distanceKm;

    // Hozir bo'shmi? — MEHMONXONA, SANATORIY uchun null
    private Boolean availableToday;
}