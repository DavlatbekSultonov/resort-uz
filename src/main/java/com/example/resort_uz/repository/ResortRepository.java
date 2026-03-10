package com.example.resort_uz.repository;

import com.example.resort_uz.entity.Resort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResortRepository extends JpaRepository<Resort, Long> {

    Page<Resort> findByActiveTrue(Pageable pageable);
    Page<Resort> findByRegionIdAndActiveTrue(Long regionId, Pageable pageable);
    List<Resort> findByFeaturedTrueAndActiveTrueOrderByAverageRatingDesc();
    Page<Resort> findByAdminId(Long adminId, Pageable pageable);
    long countByRegionIdAndActiveTrue(Long regionId);

    @Query("""
            SELECT r FROM Resort r
            WHERE r.active = true
              AND (:regionId IS NULL OR r.region.id = :regionId)
              AND (:resortType IS NULL OR r.resortType = :resortType)
              AND (:minPrice IS NULL OR r.pricePerNightMin >= :minPrice)
              AND (:maxPrice IS NULL OR r.pricePerNightMin <= :maxPrice)
              AND (:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (
                :checkIn IS NULL OR :checkOut IS NULL
                OR r.resortType = com.example.resort_uz.entity.Resort.ResortType.MEHMONXONA
                OR r.resortType = com.example.resort_uz.entity.Resort.ResortType.SANATORIY
                OR NOT EXISTS (
                    SELECT b FROM Booking b
                    WHERE b.resort.id = r.id
                      AND b.status = com.example.resort_uz.entity.Booking.BookingStatus.TASDIQLANGAN
                      AND b.checkInDate < :checkOut
                      AND b.checkOutDate > :checkIn
                )
              )
            ORDER BY r.averageRating DESC
            """)
    Page<Resort> findWithFilters(
            @Param("regionId") Long regionId,
            @Param("resortType") Resort.ResortType resortType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            Pageable pageable);

    @Modifying
    @Query("""
            UPDATE Resort r
            SET r.averageRating = (
                SELECT COALESCE(AVG(rv.rating), 0)
                FROM Review rv
                WHERE rv.resort.id = :resortId AND rv.approved = true
            ),
            r.reviewCount = (
                SELECT COUNT(rv)
                FROM Review rv
                WHERE rv.resort.id = :resortId AND rv.approved = true
            )
            WHERE r.id = :resortId
            """)
    void updateRating(@Param("resortId") Long resortId);
}