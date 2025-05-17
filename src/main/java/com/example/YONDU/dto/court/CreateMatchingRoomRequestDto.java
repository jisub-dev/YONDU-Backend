package com.example.YONDU.dto.court;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class CreateMatchingRoomRequestDto {
    private Long courtId;
    private String type; // ex: "2인 단식"
    private BigDecimal ntrpMin;
    private BigDecimal ntrpMax;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean onOffFlag;
}
