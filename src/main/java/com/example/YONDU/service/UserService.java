package com.example.YONDU.service;

import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String resetPasswordByAdmin(String targetIdentifier) {
        // 1) 대상 사용자 조회
        UserEntity user = userRepository.findById(targetIdentifier)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        // 2) 10자리 랜덤 문자열 생성
        String randomPw = generateRandomPassword(10);

        // 3) 비밀번호 해싱 후 저장
        user.setPassword(passwordEncoder.encode(randomPw));
        userRepository.save(user);

        // 4) 임시 비밀번호 반환(관리자가 보고 사용자에게 안내)
        return randomPw;
    }

    // 10자리 무작위 문자열
    private String generateRandomPassword(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 필요 시 대문자/소문자/숫자 혼합 규칙 등 추가 가능
        if (uuid.length() < length) {
            uuid += UUID.randomUUID().toString().replace("-", "");
        }
        return uuid.substring(0, length);
    }
}
