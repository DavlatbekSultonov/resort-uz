package com.example.resort_uz.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {

    @NotNull(message = "Maskan tanlansin")
    private Long resortId;

    @NotBlank(message = "Ism familiya kiritilsin")
    private String guestName;

    @NotBlank(message = "Telefon raqam kiritilsin")
    private String guestPhone;

    private String guestEmail;

    @NotNull(message = "Kelish sanasi tanlansin")
    private LocalDate checkInDate;

    @NotNull(message = "Ketish sanasi tanlansin")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Kamida 1 kishi bo'lsin")
    private Integer adultsCount;

    private Integer childrenCount;
    private Integer roomsCount;
    private String specialRequests;
}
