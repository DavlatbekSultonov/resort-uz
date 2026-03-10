package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Service_entity;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponseDTO {

    private Long id;
    private Long resortId;
    private String name;
    private String description;
    private Service_entity.ServiceType serviceType;
    private String icon;
    private Boolean isPaid;
    private BigDecimal price;
    private Service_entity.PriceType priceType;
    private String currency;
    private Boolean active;
}
