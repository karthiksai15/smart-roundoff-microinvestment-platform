package com.sromip.otp.controller;

import com.sromip.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public String generate(@RequestParam String userEmail) {
        otpService.generateOtp(userEmail);
        return "OTP sent";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String userEmail,
                         @RequestParam String otp) {

        boolean valid = otpService.verifyOtp(userEmail, otp);

        return valid ? "OTP verified" : "Invalid OTP";
    }
}