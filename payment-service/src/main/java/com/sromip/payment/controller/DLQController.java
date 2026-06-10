package com.sromip.payment.controller;

import com.sromip.payment.entity.DLQEvent;
import com.sromip.payment.repository.DLQEventRepository;
import com.sromip.payment.service.DLQService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dlq")
@RequiredArgsConstructor
public class DLQController {

    private final DLQEventRepository repository;
    private final DLQService dlqService;

    @GetMapping
    public List<DLQEvent> getAll() {
        return repository.findAll();
    }

    @PostMapping("/retry/{id}")
    public String retry(@PathVariable UUID id) {

        DLQEvent event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DLQ event not found"));

        dlqService.retryEvent(event);

        return "Retry triggered for id=" + id;
    }
}