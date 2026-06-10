package com.sromip.auth.service;

import com.sromip.auth.dto.*;
import com.sromip.auth.entity.*;
import com.sromip.auth.repository.UserRepository;
import com.sromip.auth.security.JwtUtil;
import com.sromip.auth.util.PasswordValidator;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    // ================= REGISTER =================
    public String register(RegisterRequest request) {

        PasswordValidator.validate(request.getPassword());

        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Email already exists"
                    );
                });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(AccountStatus.ACTIVE);

        userRepository.save(user);

        return "User registered successfully";
    }

    // ================= LOGIN =================
    public AuthResponse login(LoginRequest request) {

        String attemptsKey = "login_attempts:" + request.getEmail();

        Integer attempts = 0;
        String attemptsStr = redisTemplate.opsForValue().get(attemptsKey);
        if (attemptsStr != null) {
            attempts = Integer.parseInt(attemptsStr);
        }

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many attempts. Try later."
            );
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Account not active"
            );
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            redisTemplate.opsForValue().increment(attemptsKey);
            redisTemplate.expire(attemptsKey, 15, TimeUnit.MINUTES);

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        redisTemplate.delete(attemptsKey);

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        redisTemplate.opsForValue().set(
                "refresh:" + user.getEmail(),
                refreshToken,
                7, TimeUnit.DAYS
        );

        return new AuthResponse(accessToken, refreshToken);
    }

    // ================= REFRESH =================
    public AuthResponse refresh(String refreshToken) {

        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String email = jwtUtil.extractClaims(refreshToken).getSubject();

        String stored = redisTemplate.opsForValue().get("refresh:" + email);

        if (stored == null || !stored.equals(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        String newRefresh = jwtUtil.generateRefreshToken(email);

        redisTemplate.opsForValue().set(
                "refresh:" + email,
                newRefresh,
                7, TimeUnit.DAYS
        );

        String newAccess = jwtUtil.generateAccessToken(
                email,
                user.getRole().name()
        );

        return new AuthResponse(newAccess, newRefresh);
    }

    // ================= LOGOUT =================
    public String logout(String token) {

        try {
            String jti = jwtUtil.extractClaims(token).getId();

            redisTemplate.opsForValue().set(
                    "blacklist:" + jti,
                    "true",
                    15, TimeUnit.MINUTES
            );

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Logout failed"
            );
        }

        return "Logged out successfully";
    }
}