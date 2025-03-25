package com.example.YONDU.controller;

import com.example.YONDU.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // 관리자만 접근 가능 (/api/admin/**)
    @PatchMapping("/{identifier}/password")
    public ResponseEntity<?> resetUserPassword(
            @PathVariable String identifier,
            Authentication authentication // 스프링 시큐리티에서 주는 인증정보
    ) {
        // authentication.getPrincipal() → JWTAuthFilter에서 세팅한 identifier
        String adminIdentifier = (String) authentication.getPrincipal();
        // 권한: authentication.getAuthorities()

        // 비번 초기화 수행
        String newTempPw = userService.resetPasswordByAdmin(identifier);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "비밀번호 초기화 완료",
                "adminWhoReset", adminIdentifier, // 누가 초기화했는지
                "tempPassword", newTempPw
        ));
    }
}
