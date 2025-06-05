package com.example.YONDU.dto.court;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class CreateMatchingRoomRequestDto {
    private Long courtId;
    private String type;     // ex: "2인 단식" 또는 제목으로 사용
    private BigDecimal ntrpMin;
    private BigDecimal ntrpMax;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean onOffFlag;
    private String title;    // 새로 추가 (type에 저장될 예정)
    private String detail;   // 새로 추가 (현재는 자동 생성)
}
