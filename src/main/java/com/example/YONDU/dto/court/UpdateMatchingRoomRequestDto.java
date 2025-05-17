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
}
