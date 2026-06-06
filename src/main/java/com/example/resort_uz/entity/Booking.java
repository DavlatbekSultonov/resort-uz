package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi maskanga band qilish
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resort_id", nullable = false)
    private Resort resort;

    // ================================================
    //  MEHMON MA'LUMOTLARI
    //  (ro'yxatdan o'tmasdan band qiladi)
    // ================================================

    @NotBlank
    @Column(name = "guest_name", nullable = false, length = 100)
    private String guestName;

    @NotBlank
    @Column(name = "guest_phone", nullable = false, length = 20)
    private String guestPhone;

    @Column(name = "guest_email", length = 100)
    private String guestEmail;

    // ================================================
    //  BAND QILISH TAFSILOTLARI
    // ================================================

    @NotNull
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @NotNull
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Min(1)
    @Column(name = "adults_count", nullable = false)
    @Builder.Default
    private Integer adultsCount = 1;

    @Column(name = "children_count", nullable = false)
    @Builder.Default
    private Integer childrenCount = 0;

    @Column(name = "rooms_count", nullable = false)
    @Builder.Default
    private Integer roomsCount = 1;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    // ================================================
    //  NARX
    // ================================================

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(length = 5)
    @Builder.Default
    private String currency = "UZS";

    // ================================================
    //  HOLAT
    // ================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    private BookingStatus status = BookingStatus.KUTILMOQDA;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    // ================================================
    //  VAQT
    // ================================================

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BookingStatus {
        KUTILMOQDA,
        TASDIQLANGAN,
        BEKOR_QILINGAN,
        YAKUNLANGAN
    }
}
