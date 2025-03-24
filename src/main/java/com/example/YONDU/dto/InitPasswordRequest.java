package com.example.YONDU.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitPasswordRequest {
    private String token; // 인증용 임시 토큰 또는 코드
}