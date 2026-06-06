package com.example.resort_uz.repository;

import com.example.resort_uz.entity.RegionEnum;
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
    Page<Resort> findByRegionAndActiveTrue(RegionEnum region, Pageable pageable);
    List<Resort> findByFeaturedTrueAndActiveTrueOrderByAverageRatingDesc();
    Page<Resort> findByAdminId(Long adminId, Pageable pageable);
    long countByRegionAndActiveTrue(RegionEnum region);

    @Query(value = """
            SELECT r.* FROM resorts r
            WHERE r.active = true
              AND (CAST(:region AS text) IS NULL OR r.region = CAST(:region AS text))
              AND (CAST(:resortType AS text) IS NULL OR r.resort_type = CAST(:resortType AS text))
              AND (CAST(:minPrice AS numeric) IS NULL OR r.price_per_night_min >= CAST(:minPrice AS numeric))
              AND (CAST(:maxPrice AS numeric) IS NULL OR r.price_per_night_min <= CAST(:maxPrice AS numeric))
              AND (
                CAST(:search AS text) IS NULL
                OR LOWER(r.name) LIKE LOWER('%' || CAST(:search AS text) || '%')
                OR LOWER(COALESCE(r.address, '')) LIKE LOWER('%' || CAST(:search AS text) || '%')
              )
              AND (
                CAST(:checkIn AS date) IS NULL OR CAST(:checkOut AS date) IS NULL
                OR r.resort_type IN ('MEHMONXONA', 'SANATORIY')
                OR NOT EXISTS (
                    SELECT 1 FROM bookings b
                    WHERE b.resort_id = r.id
                      AND b.status = 'TASDIQLANGAN'
                      AND b.check_in_date < CAST(:checkOut AS date)
                      AND b.check_out_date > CAST(:checkIn AS date)
                )
              )
            ORDER BY r.featured DESC, r.average_rating DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM resorts r
            WHERE r.active = true
              AND (CAST(:region AS text) IS NULL OR r.region = CAST(:region AS text))
              AND (CAST(:resortType AS text) IS NULL OR r.resort_type = CAST(:resortType AS text))
              AND (CAST(:minPrice AS numeric) IS NULL OR r.price_per_night_min >= CAST(:minPrice AS numeric))
              AND (CAST(:maxPrice AS numeric) IS NULL OR r.price_per_night_min <= CAST(:maxPrice AS numeric))
              AND (
                CAST(:search AS text) IS NULL
                OR LOWER(r.name) LIKE LOWER('%' || CAST(:search AS text) || '%')
                OR LOWER(COALESCE(r.address, '')) LIKE LOWER('%' || CAST(:search AS text) || '%')
              )
              AND (
                CAST(:checkIn AS date) IS NULL OR CAST(:checkOut AS date) IS NULL
                OR r.resort_type IN ('MEHMONXONA', 'SANATORIY')
                OR NOT EXISTS (
                    SELECT 1 FROM bookings b
                    WHERE b.resort_id = r.id
                      AND b.status = 'TASDIQLANGAN'
                      AND b.check_in_date < CAST(:checkOut AS date)
                      AND b.check_out_date > CAST(:checkIn AS date)
                )
              )
            """,
            nativeQuery = true)
    Page<Resort> findWithFilters(
            @Param("region") String region,
            @Param("resortType") String resortType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search,
            @Param("checkIn") String checkIn,
            @Param("checkOut") String checkOut,
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
