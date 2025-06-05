package com.example.YONDU.dto.court;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MatchingRoomDetailDto {
    private Long id;
    private String hostId;
    private Long courtId;
    private String type;
    private BigDecimal ntrpMin;
    private BigDecimal ntrpMax;
    private Boolean onOffFlag;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdDate;
    private List<String> participants;  // 참여자 이름 목록 (현재는 ID로 임시 사용)
    private String branchName;          // 지점명 (Court.type으로 임시 사용)
    private String roomName;            // 매칭룸 이름 (type과 동일)
    private String detail;              // 자동 생성된 세부 설명
}