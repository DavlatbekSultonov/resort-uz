package com.example.resort_uz.repository;

import com.example.resort_uz.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Maskanning tasdiqlangan sharhlari
    List<Review> findByResortIdAndApprovedTrue(Long resortId);

    // Maskanning barcha sharhlari (admin uchun)
    List<Review> findByResortId(Long resortId);
}
