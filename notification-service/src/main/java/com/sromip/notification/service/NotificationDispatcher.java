package com.sromip.notification.service;

import com.sromip.notification.model.NotificationChannel;
import com.sromip.notification.service.channel.EmailSender;
import com.sromip.notification.service.channel.SmsSender;
import com.sromip.notification.service.channel.PushSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final PushSender pushSender;

    public void dispatch(
            NotificationChannel channel,
            String user,
            String message
    ) {
        switch (channel) {
            case EMAIL -> emailSender.send(user, message);
            case SMS   -> smsSender.send(user, message);
            case PUSH  -> pushSender.send(user, message);
        }
    }
}
