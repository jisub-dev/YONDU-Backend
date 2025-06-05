package com.example.YONDU.entity.court;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
@Getter @Setter
public class RoomParticipant {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "matching_room")
    private Integer matchingRoom;  // 테이블의 int 타입에 맞춤

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "status")
    private String status;

    // 컨트롤러 호환성을 위한 편의 메소드
    public void setMatchingRoomId(Long matchingRoomId) {
        this.matchingRoom = matchingRoomId != null ? matchingRoomId.intValue() : null;
    }

    public Long getMatchingRoomId() {
        return matchingRoom != null ? matchingRoom.longValue() : null;
    }
}