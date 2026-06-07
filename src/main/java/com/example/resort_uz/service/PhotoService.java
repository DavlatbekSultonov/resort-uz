package com.example.resort_uz.service;

import com.example.resort_uz.common.ApiResponse;
import com.example.resort_uz.entity.Photo;
import com.example.resort_uz.entity.Resort;
import com.example.resort_uz.repository.PhotoRepository;
import com.example.resort_uz.repository.ResortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final ResortRepository resortRepository;

    @Value("${imgbb.api.key:b652b80a8cb92deadba86915dae9d7ec}")
    private String imgbbApiKey;

    private static final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";

    @Transactional
    public ApiResponse upload(Long resortId, MultipartFile file, String caption, Boolean isCover, Long adminId) {
        try {
            Resort resort = resortRepository.findById(resortId).orElse(null);
            if (resort == null) return ApiResponse.error("Maskan topilmadi");

            // Faylni Base64 ga o'giramiz
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);

            // ImgBB ga yuklaymiz
            String imageUrl = uploadToImgBB(base64, file.getOriginalFilename());
            if (imageUrl == null) return ApiResponse.error("Rasm yuklashda xatolik");

            // Agar cover bo'lsa, eski cover ni o'chiramiz
            if (isCover) {
                photoRepository.removeCoverByResortId(resortId);
            }

            Photo photo = Photo.builder()
                    .resort(resort)
                    .url(imageUrl)
                    .isCover(isCover)
                    .build();

            Photo saved = photoRepository.save(photo);
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", saved.getId());
            map.put("url", saved.getUrl());
            map.put("thumbnailUrl", saved.getThumbnailUrl());
            map.put("caption", saved.getCaption());
            map.put("isCover", saved.getIsCover());
            map.put("sortOrder", saved.getSortOrder());
            return ApiResponse.ok("Rasm yuklandi", map);

        } catch (IOException e) {
            log.error("Rasm yuklashda xato: {}", e.getMessage());
            return ApiResponse.error("Rasm yuklashda xatolik: " + e.getMessage());
        }
    }

    private String uploadToImgBB(String base64, String filename) {
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", imgbbApiKey)
                    .addFormDataPart("image", base64)
                    .addFormDataPart("name", filename != null ? filename : "photo")
                    .build();

            Request request = new Request.Builder()
                    .url(IMGBB_UPLOAD_URL)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("ImgBB xato: {}", response.code());
                    return null;
                }
                String body = response.body().string();
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                if (json.get("success").getAsBoolean()) {
                    return json.getAsJsonObject("data").get("url").getAsString();
                }
            }
        } catch (Exception e) {
            log.error("ImgBB yuklash xato: {}", e.getMessage());
        }
        return null;
    }

    public ApiResponse getByResort(Long resortId) {
        List<Photo> photos = photoRepository.findByResortIdOrderBySortOrderAsc(resortId);
        List<java.util.Map<String, Object>> result = photos.stream().map(p -> {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("url", p.getUrl());
            map.put("thumbnailUrl", p.getThumbnailUrl());
            map.put("caption", p.getCaption());
            map.put("isCover", p.getIsCover());
            map.put("sortOrder", p.getSortOrder());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ApiResponse.ok("Rasmlar", result);
    }

    @Transactional
    public ApiResponse delete(Long photoId, Long adminId) {
        Photo photo = photoRepository.findById(photoId).orElse(null);
        if (photo == null) return ApiResponse.error("Rasm topilmadi");
        photoRepository.delete(photo);
        return ApiResponse.ok("Rasm o'chirildi");
    }

    @Transactional
    public ApiResponse setCover(Long photoId, Long resortId) {
        photoRepository.removeCoverByResortId(resortId);
        Photo photo = photoRepository.findById(photoId).orElse(null);
        if (photo == null) return ApiResponse.error("Rasm topilmadi");
        photo.setIsCover(true);
        photoRepository.save(photo);
        return ApiResponse.ok("Cover o'rnatildi");
    }
}