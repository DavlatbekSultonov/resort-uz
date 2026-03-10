package com.example.resort_uz.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {

    @NotNull
    private Long resortId;

    @NotBlank(message = "Ism kiritilsin")
    private String guestName;

    @NotNull(message = "Reyting tanlansin")
    @Min(1) @Max(5)
    private Integer rating;

    private String comment;
}
