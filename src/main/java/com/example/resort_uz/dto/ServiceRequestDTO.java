package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Service_entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ServiceRequestDTO {

    @NotNull(message = "Maskan ID si bo'sh bo'lmasin")
    private Long resortId;

    @NotBlank(message = "Xizmat nomi bo'sh bo'lmasin")
    private String name;

    private String description;

    @NotNull(message = "Xizmat turi tanlansin")
    private Service_entity.ServiceType serviceType;

    private String icon;
    private Boolean isPaid;
    private BigDecimal price;
    private Service_entity.PriceType priceType;
    private String currency;
}