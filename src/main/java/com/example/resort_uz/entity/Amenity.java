package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qulaylik nomi: "WiFi", "Bepul parking", "Konditsioner"
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Kategoriya
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AmenityCategory category;

    // Ikonka nomi — frontend uchun (masalan: "wifi", "car", "wind")
    @Column(length = 50)
    private String icon;

    // Ushbu qulaylikka ega maskanlar
    @ManyToMany(mappedBy = "amenities", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Resort> resorts = new ArrayList<>();

    public enum AmenityCategory {
        ALOQA,          // WiFi, TV, Telefon
        TRANSPORT,      // Parking, Garaj, Transfer
        IQLIM,          // Konditsioner, Isitish, Shamollatish
        OZIQ_OVQAT,     // Oshxona, Muzlatgich, Mikroto'lqin
        GIGIENA,        // Shampun, Sochiq, Fen
        XAVFSIZLIK,     // Kamera, Qo'riqchi, Seyf
        BOSHQA
    }
}
