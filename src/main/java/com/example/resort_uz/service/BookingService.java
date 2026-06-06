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
    private final TelegramService telegramService;

    @Transactional
    public ApiResponse create(BookingRequestDTO dto) {
        Resort resort = resortRepository.findById(dto.getResortId()).orElse(null);
        if (resort == null) return ApiResponse.error("Maskan topilmadi");
        if (!resort.getActive()) return ApiResponse.error("Maskan faol emas");

        if (!dto.getCheckOutDate().isAfter(dto.getCheckInDate()))
            return ApiResponse.error("Ketish sanasi kelish sanasidan keyin bo'lishi kerak");
        if (dto.getCheckInDate().isBefore(LocalDate.now()))
            return ApiResponse.error("O'tgan sanaga band qilib bo'lmaydi");

        boolean isBooked = bookingRepository.isResortBooked(
                dto.getResortId(), dto.getCheckInDate(), dto.getCheckOutDate());
        if (isBooked) return ApiResponse.error("Tanlangan sanalar band qilingan");

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
        try { telegramService.sendNewBooking(booking); } catch (Exception ignored) {}

        return ApiResponse.ok("Band qilish so'rovi yuborildi. Admin tez orada bog'lanadi!", toDTO(booking));
    }

    // Barcha bronlar — status filter bilan
    public ApiResponse getAll(Long adminId, String status, Pageable pageable) {
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin == null) return ApiResponse.error("Admin topilmadi");

        Booking.BookingStatus bookingStatus = null;
        if (status != null && !status.isBlank()) {
            try { bookingStatus = Booking.BookingStatus.valueOf(status); }
            catch (IllegalArgumentException ignored) {}
        }

        boolean isSuperAdmin = admin.getRole() == Admin.AdminRole.SUPERADMIN;
        Page<BookingResponseDTO> page;

        if (bookingStatus != null) {
            page = isSuperAdmin
                    ? bookingRepository.findByStatus(bookingStatus, pageable).map(this::toDTO)
                    : bookingRepository.findByResortAdminIdAndStatus(adminId, bookingStatus, pageable).map(this::toDTO);
        } else {
            page = isSuperAdmin
                    ? bookingRepository.findAll(pageable).map(this::toDTO)
                    : bookingRepository.findByResortAdminId(adminId, pageable).map(this::toDTO);
        }
        return ApiResponse.ok(page);
    }

    public ApiResponse getByResort(Long resortId, Long adminId, Pageable pageable) {
        Resort resort = resortRepository.findById(resortId).orElse(null);
        if (resort == null) return ApiResponse.error("Maskan topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !resort.getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");

        Page<BookingResponseDTO> page = bookingRepository.findByResortId(resortId, pageable).map(this::toDTO);
        return ApiResponse.ok(page);
    }

    public ApiResponse getPending(Long adminId, Pageable pageable) {
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin == null) return ApiResponse.error("Admin topilmadi");

        Page<BookingResponseDTO> page;
        if (admin.getRole() == Admin.AdminRole.SUPERADMIN) {
            page = bookingRepository.findByStatus(Booking.BookingStatus.KUTILMOQDA, pageable).map(this::toDTO);
        } else {
            page = bookingRepository.findByResortAdminIdAndStatus(adminId, Booking.BookingStatus.KUTILMOQDA, pageable).map(this::toDTO);
        }
        return ApiResponse.ok(page);
    }

    @Transactional
    public ApiResponse confirm(Long id, String note, Long adminId) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return ApiResponse.error("Band qilish topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !booking.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");
        if (booking.getStatus() != Booking.BookingStatus.KUTILMOQDA)
            return ApiResponse.error("Bu bron allaqachon " + booking.getStatus());

        booking.setStatus(Booking.BookingStatus.TASDIQLANGAN);
        bookingRepository.save(booking);
        return ApiResponse.ok("Band qilish tasdiqlandi");
    }

    // Bronni yakunlash
    @Transactional
    public ApiResponse complete(Long id, Long adminId) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return ApiResponse.error("Band qilish topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !booking.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");

        booking.setStatus(Booking.BookingStatus.YAKUNLANGAN);
        bookingRepository.save(booking);
        return ApiResponse.ok("Bron yakunlandi");
    }

    @Transactional
    public ApiResponse cancel(Long id, String reason, Long adminId) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return ApiResponse.error("Band qilish topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !booking.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");
        if (booking.getStatus() == Booking.BookingStatus.YAKUNLANGAN)
            return ApiResponse.error("Yakunlangan bronni bekor qilib bo'lmaydi");

        booking.setStatus(Booking.BookingStatus.BEKOR_QILINGAN);
        booking.setCancelReason(reason);
        bookingRepository.save(booking);
        return ApiResponse.ok("Band qilish bekor qilindi");
    }

    public ApiResponse getActiveBookings(Long resortId) {
        List<BookingResponseDTO> list = bookingRepository
                .findActiveBookings(resortId, LocalDate.now())
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.ok(list);
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
