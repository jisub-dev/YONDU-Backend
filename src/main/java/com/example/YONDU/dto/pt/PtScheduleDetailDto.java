package com.example.YONDU.dto.pt;

import com.example.YONDU.entity.pt.PtSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PtScheduleDetailDto {

    private Integer scheduleId;   // 세션 식별자
    private String date;          // 예약 날짜
    private String status;        // 상태

    public static PtScheduleDetailDto fromEntity(PtSession sess) {
        return new PtScheduleDetailDto(
                sess.getSessionId(),
                sess.getSessionDate().toString(),
                sess.getStatus().name()
        );
    }
}