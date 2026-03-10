package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "regions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Viloyat nomi: "Toshkent", "Samarqand", "Buxoro"
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Qisqa kod: "TSH", "SAM", "BUX" — URL va filter uchun
    @NotBlank
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    // Viloyat haqida qisqa tavsif
    @Column(columnDefinition = "TEXT")
    private String description;

    // Viloyat markazi koordinatalari — xarita uchun
    @Column(name = "center_latitude")
    private Double centerLatitude;

    @Column(name = "center_longitude")
    private Double centerLongitude;

    // Banner rasm URL (DigitalOcean serverdan)
    @Column(name = "image_url")
    private String imageUrl;

    // Ushbu viloyatdagi maskanlar
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Resort> resorts = new ArrayList<>();
}
