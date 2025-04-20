package com.example.YONDU.dto.pt;


import com.example.YONDU.entity.pt.PtSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PtScheduleDto {
    private String date;
    private String status;

    public static PtScheduleDto fromEntity(PtSession session) {
        return new PtScheduleDto(
                session.getSessionDate().toString(),
                session.getStatus().name()
        );
    }
}