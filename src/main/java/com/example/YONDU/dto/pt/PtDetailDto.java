package com.example.YONDU.dto.pt;

import com.example.YONDU.entity.pt.PtSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PtDetailDto {

    private Integer ptId;
    private Integer totalCount;
    private Integer usedCount;
    private List<PtScheduleDetailDto> schedules;

    public static PtDetailDto fromSession(PtSession sess) {
        var pkg = sess.getPtPackage();
        int used = (int) pkg.getSessions().stream()
                .filter(s -> s.getStatus() == PtSession.SessionStatus.완료)
                .count();
        List<PtScheduleDetailDto> list = pkg.getSessions().stream()
                .map(PtScheduleDetailDto::fromEntity)
                .collect(Collectors.toList());
        return new PtDetailDto(
                pkg.getPackageId(),
                pkg.getTotalSessions(),
                used,
                list
        );
    }
}
