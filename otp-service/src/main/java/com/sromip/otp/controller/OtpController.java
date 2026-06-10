package com.sromip.otp.controller;

import com.sromip.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/verify")
    public String verify(@RequestParam String otpSessionId,
                         @RequestParam String otp,
                         @RequestParam String userEmail,
                         @RequestParam String traceId) {

        boolean valid = otpService.verifyOtp(otpSessionId, otp, traceId, userEmail);

        return valid ? "OTP verified successfully" : "Invalid OTP";
    }

    // ✅ FIX: RESEND USES SAME SESSION ID
    @PostMapping("/resend")
    public String resend(@RequestParam String otpSessionId,
                         @RequestParam String userEmail,
                         @RequestParam String traceId) {

        otpService.resendOtp(otpSessionId, userEmail, traceId);

        return "OTP resent successfully";
    }
}