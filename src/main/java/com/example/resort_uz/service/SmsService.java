package com.example.resort_uz.service;

import com.example.resort_uz.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final RestTemplate restTemplate;

    @Value("${app.eskiz.email}")
    private String eskizEmail;

    @Value("${app.eskiz.password}")
    private String eskizPassword;

    @Value("${app.eskiz.sender:4546}")
    private String eskizSender;

    private static final String ESKIZ_AUTH_URL = "https://notify.eskiz.uz/api/auth/login";
    private static final String ESKIZ_SEND_URL = "https://notify.eskiz.uz/api/message/sms/send";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Token olish — form-data bilan
    private String getToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("email", eskizEmail);
            body.add("password", eskizPassword);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(ESKIZ_AUTH_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map data = (Map) response.getBody().get("data");
                if (data != null) {
                    String token = (String) data.get("token");
                    log.info("Eskiz token olindi");
                    return token;
                }
            }
            log.error("Eskiz token olishda xato: {}", response.getBody());
        } catch (Exception e) {
            log.error("Eskiz token olishda xato: {}", e.getMessage());
        }
        return null;
    }

    // SMS yuborish — form-data bilan
    private void sendSms(String phone, String message) {
        try {
            String token = getToken();
            if (token == null) {
                log.error("SMS yuborilmadi: token olinmadi");
                return;
            }

            // Telefon raqamni tozalash — faqat raqamlar
            String cleanPhone = phone.replaceAll("[^0-9]", "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(token);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("mobile_phone", cleanPhone);
            body.add("message", message);
            body.add("from", eskizSender);
            body.add("callback_url", "");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(ESKIZ_SEND_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("SMS yuborildi: {}", cleanPhone);
            } else {
                log.error("SMS yuborishda xato: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("SMS yuborishda xato: {}", e.getMessage());
        }
    }

    // Yangi band qilish — adminga va mehmonge
    public void sendBookingConfirmation(Booking booking) {
        String checkIn = booking.getCheckInDate().format(FORMATTER);
        String checkOut = booking.getCheckOutDate().format(FORMATTER);

        // Adminga (maskan telefoni)
        String adminMessage = String.format(
                "Yangi band qilish!\n" +
                        "Maskan: %s\n" +
                        "Mehmon: %s\n" +
                        "Telefon: %s\n" +
                        "Sana: %s - %s\n" +
                        "Kishilar: %d katta, %d bola\n" +
                        "Xonalar: %d%s",
                booking.getResort().getName(),
                booking.getGuestName(),
                booking.getGuestPhone(),
                checkIn, checkOut,
                booking.getAdultsCount(),
                booking.getChildrenCount(),
                booking.getRoomsCount(),
                booking.getSpecialRequests() != null && !booking.getSpecialRequests().isBlank()
                        ? "\nIzoh: " + booking.getSpecialRequests() : ""
        );
        sendSms(booking.getResort().getPhoneNumber(), adminMessage);

        // Mehmonge
        String guestMessage = String.format(
                "Hurmatli %s!\n" +
                        "%s maskaniga %s - %s\n" +
                        "kunlari uchun so'rovingiz\n" +
                        "qabul qilindi.\n" +
                        "Admin tez orada bog'lanadi.\n" +
                        "Tel: %s",
                booking.getGuestName(),
                booking.getResort().getName(),
                checkIn, checkOut,
                booking.getResort().getPhoneNumber()
        );
        sendSms(booking.getGuestPhone(), guestMessage);
    }

    // Tasdiqlash yoki bekor qilish — mehmonge
    public void sendBookingStatusUpdate(Booking booking, String note) {
        String checkIn = booking.getCheckInDate().format(FORMATTER);
        String checkOut = booking.getCheckOutDate().format(FORMATTER);
        String message;

        if (booking.getStatus() == Booking.BookingStatus.TASDIQLANGAN) {
            message = String.format(
                    "Hurmatli %s!\n" +
                            "%s maskaniga\n" +
                            "%s - %s band qilishingiz\n" +
                            "TASDIQLANDI. %s\n" +
                            "Tel: %s",
                    booking.getGuestName(),
                    booking.getResort().getName(),
                    checkIn, checkOut,
                    note != null && !note.isBlank() ? "\nIzoh: " + note : "",
                    booking.getResort().getPhoneNumber()
            );
        } else if (booking.getStatus() == Booking.BookingStatus.BEKOR_QILINGAN) {
            message = String.format(
                    "Hurmatli %s!\n" +
                            "%s maskaniga\n" +
                            "%s - %s band qilishingiz\n" +
                            "BEKOR QILINDI.%s",
                    booking.getGuestName(),
                    booking.getResort().getName(),
                    checkIn, checkOut,
                    note != null && !note.isBlank() ? "\nSabab: " + note : ""
            );
        } else {
            return;
        }

        sendSms(booking.getGuestPhone(), message);
    }
}