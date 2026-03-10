package com.sromip.otp.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpGeneratedEvent {
    private String userEmail;
    private String otp;
}