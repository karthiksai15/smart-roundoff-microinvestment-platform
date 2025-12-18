package com.sromip.notification.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String userEmail;
    private String message;
}
