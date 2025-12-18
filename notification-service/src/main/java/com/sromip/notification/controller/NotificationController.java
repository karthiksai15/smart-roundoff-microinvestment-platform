package com.sromip.notification.controller;

import com.sromip.notification.dto.NotificationRequest;
import com.sromip.notification.entity.Notification;
import com.sromip.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repo;

    @PostMapping("/send")
    public String send(@RequestBody NotificationRequest req) {
        Notification n = new Notification();
        n.setUserEmail(req.getUserEmail());
        n.setMessage(req.getMessage());
        repo.save(n);
        return "Notification sent successfully!";
    }

    @GetMapping("/list/{email}")
    public List<Notification> list(@PathVariable String email) {
        return repo.findByUserEmail(email);
    }
}
