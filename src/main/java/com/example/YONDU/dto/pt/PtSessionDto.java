package com.example.YONDU.dto.pt;

import com.example.YONDU.entity.pt.PtPackage;
import com.example.YONDU.entity.pt.PtSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PtSessionDto {
    private Integer ptId;
    private Integer totalCount;
    private Integer usedCount;
    private String trainerId;
    private List<PtScheduleDto> schedules;

    public static PtSessionDto fromPackage(PtPackage pkg) {
        List<PtScheduleDto> allSchedules = pkg.getSessions().stream()
                .map(PtScheduleDto::fromEntity)
                .collect(Collectors.toList());
        int used = (int) pkg.getSessions().stream()
                .filter(s -> s.getStatus() == PtSession.SessionStatus.완료)
                .count();
        return new PtSessionDto(
                pkg.getPackageId(),
                pkg.getTotalSessions(),
                used,
                pkg.getTrainerIdentifier(),
                allSchedules
        );
    }
}