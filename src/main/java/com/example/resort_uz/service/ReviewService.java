package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.ReviewRequestDTO;
import com.example.resort_uz.dto.ReviewResponseDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.entity.Resort;
import com.example.resort_uz.entity.Review;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.repository.ResortRepository;
import com.example.resort_uz.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ResortRepository resortRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public ApiResponse create(ReviewRequestDTO dto) {
        Resort resort = resortRepository.findById(dto.getResortId()).orElse(null);
        if (resort == null) return ApiResponse.error("Maskan topilmadi");

        Review review = Review.builder()
                .resort(resort)
                .guestName(dto.getGuestName())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .approved(true)
                .build();

        reviewRepository.save(review);
        resortRepository.updateRating(resort.getId());
        return ApiResponse.ok("Sharh yuborildi", toDTO(review));
    }

    public ApiResponse getByResort(Long resortId) {
        List<ReviewResponseDTO> list = reviewRepository
                .findByResortIdAndApprovedTrue(resortId)
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    @Transactional
    public ApiResponse delete(Long id, Long adminId) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) return ApiResponse.error("Sharh topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !review.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");

        Long resortId = review.getResort().getId();
        reviewRepository.deleteById(id);
        resortRepository.updateRating(resortId);
        return ApiResponse.ok("Sharh o'chirildi");
    }

    private ReviewResponseDTO toDTO(Review r) {
        return ReviewResponseDTO.builder()
                .id(r.getId())
                .resortId(r.getResort().getId())
                .guestName(r.getGuestName())
                .rating(r.getRating())
                .comment(r.getComment())
                .approved(r.getApproved())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
