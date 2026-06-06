package com.example.resort_uz.service;

import com.example.resort_uz.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    private final RestTemplate restTemplate;

    @Value("${app.telegram.bot-token:}")
    private String botToken;

    @Value("${app.telegram.admin-chat-id:}")
    private String adminChatId;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private void sendMessage(String chatId, String text) {
        if (botToken.isBlank() || chatId.isBlank()) {
            log.warn("Telegram sozlanmagan, xabar yuborilmadi");
            return;
        }
        try {
            String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=HTML",
                botToken, chatId, java.net.URLEncoder.encode(text, "UTF-8")
            );
            restTemplate.getForObject(url, String.class);
            log.info("Telegram xabar yuborildi: {}", chatId);
        } catch (Exception e) {
            log.error("Telegram xabar yuborishda xato: {}", e.getMessage());
        }
    }

    // Yangi bron kelganda adminga xabar
    public void sendNewBooking(Booking booking) {
        String text = String.format(
            "🏕 <b>Yangi bron so'rovi!</b>\n\n" +
            "📍 <b>Maskan:</b> %s\n" +
            "👤 <b>Mehmon:</b> %s\n" +
            "📞 <b>Telefon:</b> %s\n" +
            "📅 <b>Sana:</b> %s → %s\n" +
            "👥 <b>Kishilar:</b> %d katta, %d bola\n" +
            "🛏 <b>Xonalar:</b> %d\n" +
            "%s",
            booking.getResort().getName(),
            booking.getGuestName(),
            booking.getGuestPhone(),
            booking.getCheckInDate().format(FORMATTER),
            booking.getCheckOutDate().format(FORMATTER),
            booking.getAdultsCount(),
            booking.getChildrenCount(),
            booking.getRoomsCount(),
            booking.getSpecialRequests() != null && !booking.getSpecialRequests().isBlank()
                ? "💬 <b>Izoh:</b> " + booking.getSpecialRequests() : ""
        );
        sendMessage(adminChatId, text);
    }
}
