package com.sromip.auth.service;

import com.sromip.auth.dto.LoginRequest;
import com.sromip.auth.dto.RegisterRequest;
import com.sromip.auth.entity.User;
import com.sromip.auth.repository.UserRepository;
import com.sromip.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder; // injected

    public String register(RegisterRequest request) {
        if (repo.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        try {
            repo.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("User registration failed: " + e.getMessage());
        }

        return "User registered successfully!";
    }

    public String login(LoginRequest request) {
        User user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}

