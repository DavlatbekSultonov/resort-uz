package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi maskanga tegishli
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resort_id", nullable = false)
    private Resort resort;

    // ================================================
    //  RASM URL LARI (fayl DigitalOcean serverda)
    //  Misol: https://SERVER_IP/uploads/resorts/1/cover.jpg
    // ================================================

    // Asosiy rasm URL
    @NotBlank
    @Column(nullable = false)
    private String url;

    // Kichraytirilgan versiya URL (ro'yxat sahifalari uchun)
    // Misol: https://SERVER_IP/uploads/resorts/1/thumb_cover.jpg
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // ================================================
    //  QO'SHIMCHA MA'LUMOTLAR
    // ================================================

    // Rasm tavsifi / alt text (SEO va accessibility uchun)
    @Column(length = 200)
    private String caption;

    // Asosiy (cover) rasm — har maskanda faqat 1 ta bo'lishi kerak
    @Column(name = "is_cover", nullable = false)
    @Builder.Default
    private Boolean isCover = false;

    // Galereyadagi tartib raqami (0 dan boshlanadi)
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    // Rasm qachon yuklangan
    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

}
