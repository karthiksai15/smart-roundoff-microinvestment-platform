package com.sromip.notification.service.channel;

import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    public void send(String user, String message) {
        System.out.println("📧 EMAIL sent to " + user + " → " + message);
    }
}
