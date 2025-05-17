package com.example.YONDU.dto.court;

import com.example.YONDU.entity.court.MatchingRoom;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MatchingRoomDto(
        Long id,
        String hostId,
        Long courtId,
        String type,
        BigDecimal ntrpMin,
        BigDecimal ntrpMax,
        Boolean onOffFlag,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime createdDate
) {
    public static MatchingRoomDto from(MatchingRoom room) {
        return MatchingRoomDto.builder()
                .id(room.getId())
                .hostId(room.getHostId())
                .courtId(room.getCourtId())
                .type(room.getType())
                .ntrpMin(room.getNtrpMin())
                .ntrpMax(room.getNtrpMax())
                .onOffFlag(room.getOnOffFlag())
                .startTime(room.getStartTime())
                .endTime(room.getEndTime())
                .createdDate(room.getCreatedDate())
                .build();
    }
}
