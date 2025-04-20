package com.example.YONDU.dto.pt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PtUpdateRequestDto {
    private Integer scheduleId;  // 수정할 스케줄 ID
    private String action;       // REQUEST_COMPLETE, CANCEL, UPDATE_SCHEDULE
    private String newDate;      // 새 날짜 (ISO 문자열)
}
