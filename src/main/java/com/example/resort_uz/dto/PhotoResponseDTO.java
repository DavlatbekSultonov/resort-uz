package com.example.resort_uz.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoResponseDTO {

    private Long id;
    private Long resortId;
    private String url;
    private String thumbnailUrl;
    private String caption;
    private Boolean isCover;
    private Integer sortOrder;
    private LocalDateTime uploadedAt;
}
