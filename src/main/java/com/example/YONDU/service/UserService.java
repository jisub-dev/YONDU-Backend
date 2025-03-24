package com.example.YONDU.service;

import com.example.YONDU.entity.PasswordResetToken;
import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.PasswordResetTokenRepository;
import com.example.YONDU.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository; // 예시
    private final PasswordEncoder passwordEncoder; // Spring Security 등 활용

    @Transactional
    public void resetPassword(String identifier, String tokenValue) {
        // 1) 유저 조회
        UserEntity user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2) 토큰 검증
        //  - DB 테이블 또는 Redis 등에 { token: tokenValue, userIdentifier: ..., expiredAt: ... } 형태로 관리 가정
        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("토큰이 유효하지 않습니다."));

        if (!token.getUserIdentifier().equals(identifier)) {
            throw new RuntimeException("토큰이 해당 사용자와 일치하지 않습니다.");
        }
        if (token.isExpired()) {
            throw new RuntimeException("이미 만료된 토큰입니다.");
        }

        // 3) 새 비밀번호 생성 또는 고정값
        String newRawPassword = generateRandomPassword();  // 임의 함수
        String encodedPw = passwordEncoder.encode(newRawPassword);

        // 4) 유저 비밀번호 업데이트
        user.setPassword(encodedPw);
        userRepository.save(user);

        // 5) 토큰 사용/만료 처리
        token.markAsUsed();
        passwordResetTokenRepository.save(token);

        // 6) (선택) 임시 비밀번호 이메일 전송
        // emailService.sendPasswordResetEmail(user.getEmail(), newRawPassword);
    }

    private String generateRandomPassword() {
        // 예시: UUID로 8자리만 잘라쓰기
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}
