package com.sromip.notification.service.channel;

import org.springframework.stereotype.Service;

@Service
public class PushSender {

    public void send(String user, String message) {
        System.out.println("🔔 PUSH notification sent to " + user + " → " + message);
    }
}
