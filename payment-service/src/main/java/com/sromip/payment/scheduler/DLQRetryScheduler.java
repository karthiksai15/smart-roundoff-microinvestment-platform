package com.sromip.payment.scheduler;

import com.sromip.payment.entity.DLQEvent;
import com.sromip.payment.entity.DLQStatus;
import com.sromip.payment.repository.DLQEventRepository;
import com.sromip.payment.service.DLQService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DLQRetryScheduler {

    private final DLQEventRepository repository;
    private final DLQService dlqService;

    // 🔥 Run every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void retryFailedEvents() {

        // 🔥 ONLY FETCH EVENTS READY FOR RETRY
        List<DLQEvent> events =
                repository.findByStatusAndNextRetryAtBefore(
                        DLQStatus.NEW,
                        LocalDateTime.now()
                );

        if (events.isEmpty()) return;

        log.info("🔁 Retrying DLQ events count={}", events.size());

        for (DLQEvent event : events) {
            dlqService.retryEvent(event);
        }
    }
}