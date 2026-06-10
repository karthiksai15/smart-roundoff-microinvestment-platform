package com.sromip.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequestEvent {

    private String traceId;
    private String paymentId;

    private String otpSessionId;

    private String userEmail;


    private long expiryTime;

    private int maxAttempts;
}