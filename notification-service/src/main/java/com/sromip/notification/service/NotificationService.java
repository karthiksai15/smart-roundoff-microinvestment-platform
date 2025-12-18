package com.sromip.notification.service;

import com.sromip.notification.dto.NotificationRequest;
import com.sromip.notification.entity.Notification;
import com.sromip.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;

    public String send(NotificationRequest req) {

        Notification n = new Notification();
        n.setUserEmail(req.getUserEmail());
        n.setMessage(req.getMessage());

        repo.save(n);

        return "Notification saved!";
    }

    public List<Notification> list(String email) {
        return repo.findByUserEmail(email);
    }
}

