package com.sromip.auth.controller;

import com.sromip.auth.dto.LoginRequest;
import com.sromip.auth.dto.RegisterRequest;
import com.sromip.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        log.info("AUTH SERVICE HIT: register endpoint called for email={}", request.getEmail());
        return service.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        log.info("AUTH SERVICE HIT: login endpoint called for email={}", request.getEmail());
        return service.login(request); // ✅ RETURNS JWT
    }
}
