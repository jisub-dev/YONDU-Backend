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
    @Column(name = "matching_room")
    private Long matchingRoomId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "status")
    private String status; // enum: pending, approved, auto_canceled
}
