package com.example.resort_uz.dto;

import com.example.resort_uz.entity.Booking;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long id;

    // Maskan
    private Long resortId;
    private String resortName;
    private String resortPhone;

    // Mehmon
    private String guestName;
    private String guestPhone;
    private String guestEmail;

    // Sanalar
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer nights; // kunlar soni

    // Sig'im
    private Integer adultsCount;
    private Integer childrenCount;
    private Integer roomsCount;

    private String specialRequests;

    // Narx
    private BigDecimal totalPrice;
    private String currency;

    // Holat
    private Booking.BookingStatus status;
    private String cancelReason;

    private LocalDateTime createdAt;
}
