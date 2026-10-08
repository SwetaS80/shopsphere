package com.shopsphere.controller;

import com.shopsphere.dto.AuthDtos.*;
import com.shopsphere.entity.RefreshToken;
import com.shopsphere.entity.User;
import com.shopsphere.repository.RefreshTokenRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                           UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.getRole().name());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Refresh token expired or revoked - please log in again");
        }

        User user = stored.getUser();
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        // Rotate: revoke the old refresh token and issue a new one
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        String newRefreshToken = createRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken, user.getRole().name());
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiryMs));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
