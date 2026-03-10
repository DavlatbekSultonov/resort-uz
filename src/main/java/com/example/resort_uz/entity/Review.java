package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi maskanga tegishli
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resort_id", nullable = false)
    private Resort resort;

    // ================================================
    //  MEHMON MA'LUMOTLARI (ro'yxatdan o'tmasdan)
    // ================================================

    // Mehmon ismi (sharh yozganda kiritadi)
    @NotBlank
    @Column(name = "guest_name", nullable = false, length = 100)
    private String guestName;

    // ================================================
    //  REYTING VA SHARH
    // ================================================

    @NotNull
    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // ================================================
    //  HOLAT (admin moderatsiya qiladi)
    // ================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean approved = false;

    // ================================================
    //  VAQT
    // ================================================

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}