package com.example.resort_uz.repository;


import com.example.resort_uz.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByResortId(Long resortId, Pageable pageable);

    Page<Booking> findByStatus(Booking.BookingStatus status, Pageable pageable);

    // OWNER faqat o'z resortlarining pending bookinglarini ko'radi
    Page<Booking> findByResortAdminIdAndStatus(
            Long adminId, Booking.BookingStatus status, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.resort.id = :resortId " +
            "AND b.status != 'BEKOR_QILINGAN' " +
            "AND b.checkInDate < :checkOut AND b.checkOutDate > :checkIn")
    boolean isResortBooked(@Param("resortId") Long resortId,
                           @Param("checkIn") LocalDate checkIn,
                           @Param("checkOut") LocalDate checkOut);

    @Query("SELECT b FROM Booking b WHERE b.resort.id = :resortId " +
            "AND b.status = 'TASDIQLANGAN' " +
            "AND b.checkOutDate >= :today")
    List<Booking> findActiveBookings(@Param("resortId") Long resortId,
                                     @Param("today") LocalDate today);
}
