package com.example.YONDU.dto.court;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class UpdateMatchingRoomRequestDto {
    private Long matchingRoomId;
    private String type;
    private BigDecimal ntrpMin;
    private BigDecimal ntrpMax;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean onOffFlag;
    private String title;    // 새로 추가 (type 필드에 저장됨)
    private String detail;   // 새로 추가 (요청에는 받지만 자동 생성함)
}