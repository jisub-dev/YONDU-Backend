package com.example.YONDU.controller;

import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.UserRepository;
import com.example.YONDU.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
@RestController
public class RefreshController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RefreshController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @PostMapping("/api/tokens/refresh")
    public ResponseEntity<Map<String, Object>> refreshTokens(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("RT");

            if (refreshToken == null || refreshToken.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Refresh token is required"
                ));
            }

            Optional<UserEntity> userOptional = userRepository.findByRefreshToken(refreshToken);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Invalid refresh token"
                ));
            }

            if (!jwtService.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(998).body(Map.of(
                        "success", false,
                        "message", "Refresh token has expired"
                ));
            }

            UserEntity user = userOptional.get();
            String newAccessToken = jwtService.generateToken(user.getIdentifier(), user.getRole());
            String newRefreshToken = jwtService.generateRefreshToken(user.getIdentifier());

            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", newAccessToken,
                    "RT", newRefreshToken
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }
}
