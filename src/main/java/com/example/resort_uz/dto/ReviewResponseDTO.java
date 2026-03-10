package com.example.resort_uz.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {

    private Long id;
    private Long resortId;
    private String guestName;
    private Integer rating;
    private String comment;
    private Boolean approved;
    private LocalDateTime createdAt;
}
