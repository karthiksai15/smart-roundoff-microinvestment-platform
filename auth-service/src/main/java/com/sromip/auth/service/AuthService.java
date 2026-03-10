package com.sromip.auth.service;

import com.sromip.auth.dto.LoginRequest;
import com.sromip.auth.dto.RegisterRequest;
import com.sromip.auth.entity.User;
import com.sromip.auth.repository.UserRepository;
import com.sromip.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ================= REGISTER =================
    public String register(RegisterRequest request) {

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already exists");
                });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 🔐 HASH

        userRepository.save(user);

        return "User registered successfully";
    }

    // ================= LOGIN (JWT) =================
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password")
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // 🔐 GENERATE & RETURN JWT
        return jwtUtil.generateToken(user.getEmail());
    }
}
