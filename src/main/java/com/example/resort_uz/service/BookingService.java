package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.BookingRequestDTO;
import com.example.resort_uz.dto.BookingResponseDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.entity.Booking;
import com.example.resort_uz.entity.Resort;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.repository.BookingRepository;
import com.example.resort_uz.repository.ResortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ResortRepository resortRepository;
    private final AdminRepository adminRepository;
    private final SmsService smsService;

    // Mehmon band qiladi (token shart emas)
    @Transactional
    public ApiResponse create(BookingRequestDTO dto) {
        Resort resort = resortRepository.findById(dto.getResortId()).orElse(null);
        if (resort == null) return ApiResponse.builder()
                .status(false).message("Maskan topilmadi").build();
        if (!resort.getActive()) return ApiResponse.builder()
                .status(false).message("Maskan faol emas").build();

        if (!dto.getCheckOutDate().isAfter(dto.getCheckInDate()))
            return ApiResponse.builder()
                    .status(false).message("Ketish sanasi kelish sanasidan keyin bo'lishi kerak").build();
        if (dto.getCheckInDate().isBefore(LocalDate.now()))
            return ApiResponse.builder()
                    .status(false).message("O'tgan sanaga band qilib bo'lmaydi").build();

        boolean isBooked = bookingRepository.isResortBooked(
                dto.getResortId(), dto.getCheckInDate(), dto.getCheckOutDate());
        if (isBooked) return ApiResponse.builder()
                .status(false).message("Tanlangan sanalar band qilingan").build();

        long nights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        BigDecimal totalPrice = resort.getPricePerNightMin() != null
                ? resort.getPricePerNightMin().multiply(BigDecimal.valueOf(nights)) : null;

        Booking booking = Booking.builder()
                .resort(resort)
                .guestName(dto.getGuestName())
                .guestPhone(dto.getGuestPhone())
                .guestEmail(dto.getGuestEmail())
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .adultsCount(dto.getAdultsCount() != null ? dto.getAdultsCount() : 1)
                .childrenCount(dto.getChildrenCount() != null ? dto.getChildrenCount() : 0)
                .roomsCount(dto.getRoomsCount() != null ? dto.getRoomsCount() : 1)
                .specialRequests(dto.getSpecialRequests())
                .totalPrice(totalPrice)
                .currency(resort.getCurrency())
                .status(Booking.BookingStatus.KUTILMOQDA)
                .build();

        bookingRepository.save(booking);
        smsService.sendBookingConfirmation(booking);

        return ApiResponse.builder()
                .status(true).message("Band qilish so'rovi yuborildi").data(toDTO(booking)).build();
    }

    // OWNER o'z resortining bookinglarini ko'radi
    public ApiResponse getByResort(Long resortId, Long adminId, Pageable pageable) {
        Resort resort = resortRepository.findById(resortId).orElse(null);
        if (resort == null) return ApiResponse.builder()
                .status(false).message("Maskan topilmadi").build();

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !resort.getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        Page<BookingResponseDTO> page = bookingRepository
                .findByResortId(resortId, pageable).map(this::toDTO);
        return ApiResponse.builder().status(true).message("OK").data(page).build();
    }

    // Kutilayotgan bookinglar — SUPERADMIN barcha, OWNER faqat o'ziniki
    public ApiResponse getPending(Long adminId, Pageable pageable) {
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();

        Page<BookingResponseDTO> page;
        if (admin.getRole() == Admin.AdminRole.SUPERADMIN) {
            page = bookingRepository
                    .findByStatus(Booking.BookingStatus.KUTILMOQDA, pageable).map(this::toDTO);
        } else {
            page = bookingRepository
                    .findByResortAdminIdAndStatus(adminId, Booking.BookingStatus.KUTILMOQDA, pageable)
                    .map(this::toDTO);
        }
        return ApiResponse.builder().status(true).message("OK").data(page).build();
    }

    // OWNER faqat o'z resortining bookingini tasdiqlaydi
    @Transactional
    public ApiResponse confirm(Long id, String note, Long adminId) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return ApiResponse.builder()
                .status(false).message("Band qilish topilmadi").build();

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !booking.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        if (booking.getStatus() != Booking.BookingStatus.KUTILMOQDA)
            return ApiResponse.builder()
                    .status(false).message("Bu band qilish allaqachon " + booking.getStatus()).build();

        booking.setStatus(Booking.BookingStatus.TASDIQLANGAN);
        bookingRepository.save(booking);
        smsService.sendBookingStatusUpdate(booking, note);

        return ApiResponse.builder().status(true).message("Band qilish tasdiqlandi").build();
    }

    // OWNER faqat o'z resortining bookingini bekor qiladi
    @Transactional
    public ApiResponse cancel(Long id, String reason, Long adminId) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return ApiResponse.builder()
                .status(false).message("Band qilish topilmadi").build();

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !booking.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        if (booking.getStatus() == Booking.BookingStatus.YAKUNLANGAN)
            return ApiResponse.builder()
                    .status(false).message("Yakunlangan band qilishni bekor qilib bo'lmaydi").build();

        booking.setStatus(Booking.BookingStatus.BEKOR_QILINGAN);
        booking.setCancelReason(reason);
        bookingRepository.save(booking);
        smsService.sendBookingStatusUpdate(booking, reason);

        return ApiResponse.builder().status(true).message("Band qilish bekor qilindi").build();
    }

    public ApiResponse getActiveBookings(Long resortId) {
        List<BookingResponseDTO> list = bookingRepository
                .findActiveBookings(resortId, LocalDate.now())
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.builder().status(true).message("OK").data(list).build();
    }

    private BookingResponseDTO toDTO(Booking b) {
        long nights = ChronoUnit.DAYS.between(b.getCheckInDate(), b.getCheckOutDate());
        return BookingResponseDTO.builder()
                .id(b.getId())
                .resortId(b.getResort().getId())
                .resortName(b.getResort().getName())
                .resortPhone(b.getResort().getPhoneNumber())
                .guestName(b.getGuestName())
                .guestPhone(b.getGuestPhone())
                .guestEmail(b.getGuestEmail())
                .checkInDate(b.getCheckInDate())
                .checkOutDate(b.getCheckOutDate())
                .nights((int) nights)
                .adultsCount(b.getAdultsCount())
                .childrenCount(b.getChildrenCount())
                .roomsCount(b.getRoomsCount())
                .specialRequests(b.getSpecialRequests())
                .totalPrice(b.getTotalPrice())
                .currency(b.getCurrency())
                .status(b.getStatus())
                .cancelReason(b.getCancelReason())
                .createdAt(b.getCreatedAt())
                .build();
    }
}