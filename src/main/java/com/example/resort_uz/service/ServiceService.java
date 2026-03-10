package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.ServiceRequestDTO;
import com.example.resort_uz.dto.ServiceResponseDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.entity.Resort;
import com.example.resort_uz.entity.Service_entity;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.repository.ResortRepository;
import com.example.resort_uz.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ResortRepository resortRepository;
    private final AdminRepository adminRepository;

    public ApiResponse getByResort(Long resortId) {
        List<ServiceResponseDTO> list = serviceRepository
                .findByResortIdAndActiveTrue(resortId)
                .stream().map((Service s) -> toDTO((Service_entity) s))
                .collect(Collectors.toList());
        return ApiResponse.builder().status(true).message("OK").data(list).build();
    }

    public ApiResponse create(ServiceRequestDTO dto, Long adminId) {
        Resort resort = resortRepository.findById(dto.getResortId()).orElse(null);
        if (resort == null) return ApiResponse.builder()
                .status(false).message("Maskan topilmadi").build();

        // OWNER faqat o'z resortiga xizmat qo'sha oladi
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin == null) return ApiResponse.builder()
                .status(false).message("Admin topilmadi").build();

        boolean isSuperAdmin = admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !resort.getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        Service_entity service = Service_entity.builder()
                .resort(resort)
                .name(dto.getName())
                .description(dto.getDescription())
                .serviceType(dto.getServiceType())
                .icon(dto.getIcon())
                .isPaid(dto.getIsPaid() != null ? dto.getIsPaid() : false)
                .price(dto.getPrice())
                .priceType(dto.getPriceType())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "UZS")
                .active(true)
                .build();

        serviceRepository.save(service);
        return ApiResponse.builder().status(true).message("Xizmat qo'shildi")
                .data(toDTO(service)).build();
    }

    public ApiResponse update(Long id, ServiceRequestDTO dto, Long adminId) {
        Service_entity service = serviceRepository.findById(id).orElse(null);
        if (service == null) return ApiResponse.builder()
                .status(false).message("Xizmat topilmadi").build();

        // OWNER faqat o'z resortining xizmatini tahrirlaydi
        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !service.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setServiceType(dto.getServiceType());
        service.setIcon(dto.getIcon());
        if (dto.getIsPaid() != null) service.setIsPaid(dto.getIsPaid());
        service.setPrice(dto.getPrice());
        service.setPriceType(dto.getPriceType());
        if (dto.getCurrency() != null) service.setCurrency(dto.getCurrency());
        serviceRepository.save(service);

        return ApiResponse.builder().status(true).message("Xizmat yangilandi")
                .data(toDTO(service)).build();
    }

    public ApiResponse delete(Long id, Long adminId) {
        Service_entity service = serviceRepository.findById(id).orElse(null);
        if (service == null) return ApiResponse.builder()
                .status(false).message("Xizmat topilmadi").build();

        // OWNER faqat o'z resortining xizmatini o'chiradi
        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !service.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.builder().status(false).message("Ruxsat yo'q").build();

        service.setActive(false);
        serviceRepository.save(service);
        return ApiResponse.builder().status(true).message("Xizmat o'chirildi").build();
    }

    private ServiceResponseDTO toDTO(Service_entity s) {
        return ServiceResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .serviceType(s.getServiceType())
                .icon(s.getIcon())
                .isPaid(s.getIsPaid())
                .price(s.getPrice())
                .priceType(s.getPriceType())
                .currency(s.getCurrency())
                .build();
    }
}