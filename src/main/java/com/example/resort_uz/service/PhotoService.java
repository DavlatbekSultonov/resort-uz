package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.dto.PhotoResponseDTO;
import com.example.resort_uz.entity.Admin;
import com.example.resort_uz.entity.Photo;
import com.example.resort_uz.entity.Resort;
import com.example.resort_uz.repository.AdminRepository;
import com.example.resort_uz.repository.PhotoRepository;
import com.example.resort_uz.repository.ResortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final ResortRepository resortRepository;
    private final AdminRepository adminRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public ApiResponse upload(Long resortId, MultipartFile file, String caption, Boolean isCover, Long adminId) {
        Resort resort = resortRepository.findById(resortId).orElse(null);
        if (resort == null) return ApiResponse.error("Maskan topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin == null) return ApiResponse.error("Admin topilmadi");

        boolean isSuperAdmin = admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !resort.getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");
        if (file.isEmpty()) return ApiResponse.error("Fayl bo'sh");

        String ext = getExtension(file.getOriginalFilename());
        if (!isAllowedExtension(ext))
            return ApiResponse.error("Faqat jpg, jpeg, png, webp formatlar ruxsat etilgan");

        try {
            String folderPath = uploadDir + File.separator + "resorts" + File.separator + resortId;
            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder)) Files.createDirectories(folder);

            String fileName = UUID.randomUUID() + "." + ext;
            Path filePath = folder.resolve(fileName);
            Files.write(filePath, file.getBytes());

            String url = baseUrl + "/uploads/resorts/" + resortId + "/" + fileName;

            if (Boolean.TRUE.equals(isCover)) photoRepository.removeCoverByResortId(resortId);

            int sortOrder = photoRepository.countByResortId(resortId);

            Photo photo = Photo.builder()
                    .resort(resort)
                    .url(url)
                    .caption(caption)
                    .isCover(Boolean.TRUE.equals(isCover))
                    .sortOrder(sortOrder)
                    .build();

            photoRepository.save(photo);
            return ApiResponse.ok("Rasm yuklandi", toDTO(photo));
        } catch (IOException e) {
            log.error("Rasm yuklashda xato: {}", e.getMessage());
            return ApiResponse.error("Rasm yuklashda xato yuz berdi");
        }
    }

    public ApiResponse getByResort(Long resortId) {
        List<PhotoResponseDTO> list = photoRepository
                .findByResortIdOrderBySortOrderAsc(resortId)
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    @Transactional
    public ApiResponse setCover(Long photoId, Long adminId) {
        Photo photo = photoRepository.findById(photoId).orElse(null);
        if (photo == null) return ApiResponse.error("Rasm topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !photo.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");

        photoRepository.removeCoverByResortId(photo.getResort().getId());
        photo.setIsCover(true);
        photoRepository.save(photo);
        return ApiResponse.ok("Cover rasm o'rnatildi");
    }

    @Transactional
    public ApiResponse delete(Long photoId, Long adminId) {
        Photo photo = photoRepository.findById(photoId).orElse(null);
        if (photo == null) return ApiResponse.error("Rasm topilmadi");

        Admin admin = adminRepository.findById(adminId).orElse(null);
        boolean isSuperAdmin = admin != null && admin.getRole() == Admin.AdminRole.SUPERADMIN;
        if (!isSuperAdmin && !photo.getResort().getAdmin().getId().equals(adminId))
            return ApiResponse.error("Ruxsat yo'q");

        try {
            String filePath = photo.getUrl()
                    .replace(baseUrl + "/uploads", uploadDir)
                    .replace("/", File.separator);
            Path path = Paths.get(filePath);
            if (Files.exists(path)) Files.delete(path);
        } catch (IOException e) {
            log.warn("Faylni o'chirishda xato: {}", e.getMessage());
        }

        photoRepository.deleteById(photoId);
        return ApiResponse.ok("Rasm o'chirildi");
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String ext) {
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("webp");
    }

    private PhotoResponseDTO toDTO(Photo p) {
        return PhotoResponseDTO.builder()
                .id(p.getId())
                .resortId(p.getResort().getId())
                .url(p.getUrl())
                .thumbnailUrl(p.getThumbnailUrl())
                .caption(p.getCaption())
                .isCover(p.getIsCover())
                .sortOrder(p.getSortOrder())
                .uploadedAt(p.getUploadedAt())
                .build();
    }
}
