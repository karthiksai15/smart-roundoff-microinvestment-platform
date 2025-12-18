package com.sromip.auth.controller;

import com.sromip.auth.dto.LoginRequest;
import com.sromip.auth.dto.RegisterRequest;
import com.sromip.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        return service.register(request);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        System.out.println("Received email = " + request.getEmail());
        System.out.println("Received password = " + request.getPassword());
        return service.login(request);
    }



}
