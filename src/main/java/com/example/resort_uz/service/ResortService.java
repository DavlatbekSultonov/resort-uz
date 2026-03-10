package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.*;
import com.example.resort_uz.entity.*;
import com.example.resort_uz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResortService {

    private final ResortRepository resortRepository;
    private final RegionRepository regionRepository;
    private final AdminRepository adminRepository;
    private final AmenityRepository amenityRepository;
    private final PhotoRepository photoRepository;
    private final BookingRepository bookingRepository;

    // ================================================
    //  USER PANEL
    // ================================================

    public ApiResponse getAll(Pageable pageable) {
        Page<com.example.resort_uz.dto.response.ResortCardDTO> page = resortRepository.findByActiveTrue(pageable)
                .map(r -> toCardDTO(r, null, null, null));
        return ApiResponse.builder().status(true).message("OK").data(page).build();
    }

    public ApiResponse filter(Long regionId, String resortType,
                              BigDecimal minPrice, BigDecimal maxPrice,
                              String search,
                              LocalDate checkIn, LocalDate checkOut,
                              Double userLat, Double userLon,
                              Pageable pageable) {
        Resort.ResortType type = null;
        if (resortType != null && !resortType.isBlank()) {
            try { type = Resort.ResortType.valueOf(resortType); }
            catch (IllegalArgumentException ignored) {}
        }

        Page<com.example.resort_uz.dto.response.ResortCardDTO> page = resortRepository
                .findWithFilters(regionId, type, minPrice, maxPrice, search, checkIn, checkOut, pageable)
                .map(r -> toCardDTO(r, checkIn, checkOut, userLat != null && userLon != null
                        ? new double[]{userLat, userLon} : null));

        return ApiResponse.builder().status(true).message("OK").data(page).build();
    }

    public ApiResponse getById(Long id) {
        Resort resort = resortRepository.findById(id).orElse(null);
        if (resort == null || !resort.getActive()) return ApiResponse.builder()
                .status(false).message("Maskan topilmadi").build();
        return ApiResponse.builder().status(true).message("OK").data(toResponseDTO(resort)).build();
    }

    public ApiResponse getFeatured() {
        List<com.example.resort_uz.dto.response.ResortCardDTO> list = resortRepository
                .findByFeaturedTrueAndActiveTrueOrderByAverageRatingDesc()
                .stream().map(r -> toCardDTO(r, null, null, null))
                .collect(Collectors.toList());
        return ApiResponse.builder().status(true).message("OK").data(list).build();
    }

    // ================================================
    //  ADMIN PANEL
    // ================================================

    public ApiResponse getByAdmin(Long adminId, Pageable pageable) {
        Page<com.example.resort_uz.dto.response.ResortCardDTO> page = resortRepository.findByAdminId(adminId, pageable)
                .map(r -> toCardDTO(r, null, null, null));
        return ApiResponse.builder().status(true).message("OK").data(page).build();
    }

    @Transactional
    public ApiResponse create(ResortRequestDTO dto, Long adminId) {
        Admin requestingAdmin = adminRepository.findById(adminId).orElse(null);
        if (requestingAdmin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();

        Admin owner;
        if (requestingAdmin.getRole() == Admin.AdminRole.SUPERADMIN && dto.getOwnerId() != null) {
            owner = adminRepository.findById(dto.getOwnerId()).orElse(null);
            if (owner == null) return ApiResponse.builder()
                    .status(false).message("Owner topilmadi").build();
        } else {
            owner = requestingAdmin;
        }

        Region region = regionRepository.findById(dto.getRegionId()).orElse(null);
        if (region == null) return ApiResponse.builder()
                .status(false).message("Viloyat topilmadi").build();

        List<Amenity> amenities = new ArrayList<>();
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            amenities = amenityRepository.findAllById(dto.getAmenityIds());
        }

        Resort resort = Resort.builder()
                .admin(owner)
                .region(region)
                .name(dto.getName())
                .shortDescription(dto.getShortDescription())
                .fullDescription(dto.getFullDescription())
                .resortType(dto.getResortType())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .distanceFromTashkent(dto.getDistanceFromTashkent())
                .pricePerNightMin(dto.getPricePerNightMin())
                .pricePerNightMax(dto.getPricePerNightMax())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "UZS")
                .phoneNumber(dto.getPhoneNumber())
                .phoneNumber2(dto.getPhoneNumber2())
                .email(dto.getEmail())
                .websiteUrl(dto.getWebsiteUrl())
                .telegramLink(dto.getTelegramLink())
                .instagramLink(dto.getInstagramLink())
                .maxCapacity(dto.getMaxCapacity())
                .roomCount(dto.getRoomCount())
                .cottageCount(dto.getCottageCount())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .featured(dto.getFeatured() != null ? dto.getFeatured() : false)
                .openYearRound(dto.getOpenYearRound() != null ? dto.getOpenYearRound() : true)
                .openMonth(dto.getOpenMonth())
                .closeMonth(dto.getCloseMonth())
                .amenities(amenities)
                .build();

        resortRepository.save(resort);
        return ApiResponse.builder().status(true).message("Maskan qo'shildi")
                .data(toResponseDTO(resort)).build();
    }

    @Transactional
    public ApiResponse update(Long id, ResortRequestDTO dto, Long adminId) {
        Resort resort = resortRepository.findById(id).orElse(null);
        if (resort == null) return ApiResponse.builder()
                .status(false).message("Maskan topilmadi").build();

        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();

        boolean isSuperAdmin = admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !resort.getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        Region region = regionRepository.findById(dto.getRegionId()).orElse(null);
        if (region == null) return ApiResponse.builder()
                .status(false).message("Viloyat topilmadi").build();

        resort.setRegion(region);
        resort.setName(dto.getName());
        resort.setShortDescription(dto.getShortDescription());
        resort.setFullDescription(dto.getFullDescription());
        resort.setResortType(dto.getResortType());
        resort.setAddress(dto.getAddress());
        resort.setLatitude(dto.getLatitude());
        resort.setLongitude(dto.getLongitude());
        resort.setDistanceFromTashkent(dto.getDistanceFromTashkent());
        resort.setPricePerNightMin(dto.getPricePerNightMin());
        resort.setPricePerNightMax(dto.getPricePerNightMax());
        if (dto.getCurrency() != null) resort.setCurrency(dto.getCurrency());
        resort.setPhoneNumber(dto.getPhoneNumber());
        resort.setPhoneNumber2(dto.getPhoneNumber2());
        resort.setEmail(dto.getEmail());
        resort.setWebsiteUrl(dto.getWebsiteUrl());
        resort.setTelegramLink(dto.getTelegramLink());
        resort.setInstagramLink(dto.getInstagramLink());
        resort.setMaxCapacity(dto.getMaxCapacity());
        resort.setRoomCount(dto.getRoomCount());
        resort.setCottageCount(dto.getCottageCount());
        if (dto.getActive() != null) resort.setActive(dto.getActive());
        if (dto.getFeatured() != null) resort.setFeatured(dto.getFeatured());
        if (dto.getOpenYearRound() != null) resort.setOpenYearRound(dto.getOpenYearRound());
        resort.setOpenMonth(dto.getOpenMonth());
        resort.setCloseMonth(dto.getCloseMonth());
        if (dto.getAmenityIds() != null) {
            resort.setAmenities(amenityRepository.findAllById(dto.getAmenityIds()));
        }

        resortRepository.save(resort);
        return ApiResponse.builder().status(true).message("Maskan yangilandi")
                .data(toResponseDTO(resort)).build();
    }

    @Transactional
    public ApiResponse delete(Long id, Long adminId) {
        Resort resort = resortRepository.findById(id).orElse(null);
        if (resort == null) return ApiResponse.builder()
                .status(false).message("Maskan topilmadi").build();

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !resort.getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        resortRepository.deleteById(id);
        return ApiResponse.builder().status(true).message("Maskan o'chirildi").build();
    }

    @Transactional
    public ApiResponse toggleActive(Long id) {
        Resort resort = resortRepository.findById(id).orElse(null);
        if (resort == null) return ApiResponse.builder()
                .status(false).message("Maskan topilmadi").build();
        resort.setActive(!resort.getActive());
        resortRepository.save(resort);
        return ApiResponse.builder().status(true)
                .message(resort.getActive() ? "Maskan faollashtirildi" : "Maskan o'chirildi").build();
    }

    // ================================================
    //  ENTITY -> DTO
    // ================================================

    private com.example.resort_uz.dto.response.ResortCardDTO toCardDTO(Resort r, LocalDate checkIn, LocalDate checkOut, double[] userCoords) {
        String cover = photoRepository.findByResortIdAndIsCoverTrue(r.getId())
                .map(Photo::getUrl).orElse(null);

        // availableToday — MEHMONXONA, SANATORIY uchun null
        Boolean availableToday = null;
        boolean needsCalendar = r.getResortType() != Resort.ResortType.MEHMONXONA
                && r.getResortType() != Resort.ResortType.SANATORIY;

        if (needsCalendar) {
            LocalDate from = checkIn != null ? checkIn : LocalDate.now();
            LocalDate to = checkOut != null ? checkOut : LocalDate.now().plusDays(1);
            boolean booked = bookingRepository.isResortBooked(r.getId(), from, to);
            availableToday = !booked;
        }

        // Masofa hisoblash — userCoords yuborilsa
        Double distanceKm = null;
        if (userCoords != null && r.getLatitude() != null && r.getLongitude() != null) {
            distanceKm = haversineKm(userCoords[0], userCoords[1], r.getLatitude(), r.getLongitude());
        }

        return com.example.resort_uz.dto.response.ResortCardDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .resortType(r.getResortType())
                .regionName(r.getRegion() != null ? r.getRegion().getName() : null)
                .address(r.getAddress())
                .pricePerNightMin(r.getPricePerNightMin())
                .currency(r.getCurrency())
                .averageRating(r.getAverageRating())
                .reviewCount(r.getReviewCount())
                .featured(r.getFeatured())
                .coverImageUrl(cover)
                .availableToday(availableToday)
                .distanceKm(distanceKm != null ? Math.round(distanceKm * 10.0) / 10.0 : null)
                .build();
    }

    // Haversine formulasi — ikki nuqta orasidagi masofa (km)
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private ResortResponseDTO toResponseDTO(Resort r) {
        String cover = photoRepository.findByResortIdAndIsCoverTrue(r.getId())
                .map(Photo::getUrl).orElse(null);

        List<PhotoResponseDTO> photos = photoRepository
                .findByResortIdOrderBySortOrderAsc(r.getId())
                .stream().map(p -> PhotoResponseDTO.builder()
                        .id(p.getId())
                        .url(p.getUrl())
                        .isCover(p.getIsCover())
                        .sortOrder(p.getSortOrder())
                        .caption(p.getCaption())
                        .uploadedAt(p.getUploadedAt())
                        .build())
                .collect(Collectors.toList());

        List<AmenityResponseDTO> amenities = r.getAmenities() == null ? List.of() :
                r.getAmenities().stream().map(a -> AmenityResponseDTO.builder()
                        .id(a.getId()).name(a.getName())
                        .category(a.getCategory()).icon(a.getIcon())
                        .build()).collect(Collectors.toList());

        List<ServiceResponseDTO> services = r.getServices() == null ? List.of() :
                r.getServices().stream()
                        .filter(s -> Boolean.TRUE.equals(s.getActive()))
                        .map(s -> ServiceResponseDTO.builder()
                                .id(s.getId()).name(s.getName())
                                .description(s.getDescription())
                                .serviceType(s.getServiceType()).icon(s.getIcon())
                                .isPaid(s.getIsPaid()).price(s.getPrice())
                                .priceType(s.getPriceType()).currency(s.getCurrency())
                                .build())
                        .collect(Collectors.toList());

        return ResortResponseDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .shortDescription(r.getShortDescription())
                .fullDescription(r.getFullDescription())
                .resortType(r.getResortType())
                .regionId(r.getRegion() != null ? r.getRegion().getId() : null)
                .regionName(r.getRegion() != null ? r.getRegion().getName() : null)
                .address(r.getAddress())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .distanceFromTashkent(r.getDistanceFromTashkent())
                .pricePerNightMin(r.getPricePerNightMin())
                .pricePerNightMax(r.getPricePerNightMax())
                .currency(r.getCurrency())
                .phoneNumber(r.getPhoneNumber())
                .phoneNumber2(r.getPhoneNumber2())
                .email(r.getEmail())
                .websiteUrl(r.getWebsiteUrl())
                .telegramLink(r.getTelegramLink())
                .instagramLink(r.getInstagramLink())
                .maxCapacity(r.getMaxCapacity())
                .roomCount(r.getRoomCount())
                .cottageCount(r.getCottageCount())
                .averageRating(r.getAverageRating())
                .reviewCount(r.getReviewCount())
                .active(r.getActive())
                .featured(r.getFeatured())
                .openYearRound(r.getOpenYearRound())
                .openMonth(r.getOpenMonth())
                .closeMonth(r.getCloseMonth())
                .coverImageUrl(cover)
                .photos(photos)
                .amenities(amenities)
                .services(services)
                .createdAt(r.getCreatedAt())
                .build();
    }
}