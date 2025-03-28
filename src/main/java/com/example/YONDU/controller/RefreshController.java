package com.example.YONDU.controller;

import com.example.YONDU.dto.LogginDto;
import com.example.YONDU.entity.Bank;
import com.example.YONDU.entity.ReceiptBoolean;
import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.UserRepository;
import com.example.YONDU.security.CustomUserDetails;
import com.example.YONDU.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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

    @PatchMapping("/api/modify-info")
    public ResponseEntity<Map<String, Object>> modifyInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestBody Map<String, String> request) {
        try {
            // 1. 현재 인증된 사용자 정보 가져오기
            UserEntity user = userDetails.getUser();

            // 2. 변경하고자 하는 값이 request 바디에 있으면 해당 필드만 수정
            if (request.containsKey("password")) {
                String rawPassword = request.get("password");
                user.setPassword(passwordEncoder.encode(rawPassword));
            }

            if (request.containsKey("refundBank")) {
                String refundBank = request.get("refundBank");
                Bank selectedBank = Bank.fromString(refundBank); // 문자열 -> Bank enum
                user.setRefundBank(selectedBank);
            }

            if (request.containsKey("refundAccount")) {
                user.setRefundAccount(request.get("refundAccount"));
            }

            if (request.containsKey("receiptInfo")) {
                user.setReceiptInfo(ReceiptBoolean.valueOf(request.get("receiptInfo")));
            }

            if (request.containsKey("phone")) {
                String newPhone = request.get("phone");
                user.setPhone(newPhone);
            }

            // 3. 수정 시간을 갱신(UpdatedAt)
            user.setUpdatedAt(LocalDateTime.now());

            // 4. DB에 저장
            userRepository.save(user);

            // 5. 수정 완료 후 클라이언트에 성공 응답
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User info updated",
                    "user", new LogginDto(user)
            ));

        } catch (Exception e) {
            // 서버에서 예외가 난 경우, 500 에러 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }
}
