package com.example.resort_uz.repository;

import com.example.resort_uz.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.resort WHERE b.resort.id = :resortId")
    Page<Booking> findByResortId(@Param("resortId") Long resortId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.resort WHERE b.status = :status")
    Page<Booking> findByStatus(@Param("status") Booking.BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.resort WHERE b.resort.admin.id = :adminId AND b.status = :status")
    Page<Booking> findByResortAdminIdAndStatus(
            @Param("adminId") Long adminId,
            @Param("status") Booking.BookingStatus status,
            Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.resort WHERE b.resort.admin.id = :adminId")
    Page<Booking> findByResortAdminId(@Param("adminId") Long adminId, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.resort.id = :resortId " +
            "AND b.status != 'BEKOR_QILINGAN' " +
            "AND b.checkInDate < :checkOut AND b.checkOutDate > :checkIn")
    boolean isResortBooked(@Param("resortId") Long resortId,
                           @Param("checkIn") LocalDate checkIn,
                           @Param("checkOut") LocalDate checkOut);

    @Query("SELECT b FROM Booking b JOIN FETCH b.resort WHERE b.resort.id = :resortId " +
            "AND b.status = 'TASDIQLANGAN' " +
            "AND b.checkOutDate >= :today")
    List<Booking> findActiveBookings(@Param("resortId") Long resortId,
                                     @Param("today") LocalDate today);

    // ✅ Muddati o'tgan tasdiqlangan bronlar — YAKUNLANGAN ga o'tkazish uchun
    @Modifying
    @Query("UPDATE Booking b SET b.status = 'YAKUNLANGAN' " +
            "WHERE b.status = 'TASDIQLANGAN' AND b.checkOutDate < :today")
    int completeExpiredBookings(@Param("today") LocalDate today);
}
