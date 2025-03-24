package com.example.YONDU.controller;

import com.example.YONDU.dto.InitPasswordRequest;
import com.example.YONDU.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 비밀번호 초기화
    @PatchMapping("/{identifier}/password")
    public ResponseEntity<?> initPassword(
            @PathVariable String identifier,
            @RequestBody InitPasswordRequest request
    ) {
        userService.resetPassword(identifier, request.getToken());

        return ResponseEntity.ok().body(
                new ApiResponse(200, true, "비밀번호 초기화 성공")
        );
    }

}

// 샘플 응답 DTO
class ApiResponse {
    private int status;
    private boolean success;
    private String message;

    public ApiResponse(int status, boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }
}
