package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resorts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Resort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false, length = 50)
    private RegionEnum region;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "resort_type", nullable = false, length = 50)
    @Builder.Default
    private ResortType resortType = ResortType.DAM_OLISH_MASKANI;

    @Column(nullable = false, length = 300)
    private String address;

    private Double latitude;
    private Double longitude;

    @Column(name = "distance_from_tashkent")
    private Double distanceFromTashkent;

    @Column(name = "price_per_night_min")
    private BigDecimal pricePerNightMin;

    @Column(name = "price_per_night_max")
    private BigDecimal pricePerNightMax;

    @Column(length = 10)
    @Builder.Default
    private String currency = "UZS";

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "phone_number2", length = 20)
    private String phoneNumber2;

    @Column(length = 100)
    private String email;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "telegram_link")
    private String telegramLink;

    @Column(name = "instagram_link")
    private String instagramLink;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "room_count")
    private Integer roomCount;

    @Column(name = "cottage_count")
    private Integer cottageCount;

    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "open_year_round")
    @Builder.Default
    private Boolean openYearRound = true;

    @Column(name = "open_month")
    private Integer openMonth;

    @Column(name = "close_month")
    private Integer closeMonth;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "resort", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "resort", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "resort", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Service_entity> services = new ArrayList<>();

    @OneToMany(mappedBy = "resort", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "resort_amenities",
            joinColumns = @JoinColumn(name = "resort_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @Builder.Default
    private List<Amenity> amenities = new ArrayList<>();

    public enum ResortType {
        DAM_OLISH_MASKANI, SANATORIY, MEHMONXONA, KOTEJ,
        TURISTIK_BAZA, AGROTURIZM, TOGLIK_RESORT, SUV_YONI_RESORT
    }
}