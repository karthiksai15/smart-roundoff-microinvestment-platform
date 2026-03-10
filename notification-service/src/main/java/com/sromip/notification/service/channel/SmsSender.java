package com.sromip.notification.service.channel;

import org.springframework.stereotype.Service;

@Service
public class SmsSender {

    public void send(String user, String message) {
        System.out.println("📱 SMS sent to " + user + " → " + message);
    }
}
