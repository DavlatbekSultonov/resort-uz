package com.example.resort_uz.scheduler;

import com.example.resort_uz.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingRepository bookingRepository;

    // Har kuni 00:05 da muddati o'tgan bronlarni YAKUNLANGAN ga o'tkazadi
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void completeExpiredBookings() {
        int count = bookingRepository.completeExpiredBookings(LocalDate.now());
        if (count > 0) {
            log.info("Muddati o'tgan {} ta bron YAKUNLANGAN ga o'tkazildi", count);
        }
    }
}
