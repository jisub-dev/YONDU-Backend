package com.example.YONDU.repository.court;

import com.example.YONDU.entity.court.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {
    boolean existsByMatchingRoomIdAndUserId(Long roomId, String userId);
    Optional<RoomParticipant> findByMatchingRoomIdAndUserId(Long roomId, String userId);
}
