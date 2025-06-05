package com.example.YONDU.repository.court;

import com.example.YONDU.entity.court.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, String> {

    // 테이블 구조에 맞는 실제 메소드들
    @Query("SELECT p FROM RoomParticipant p WHERE p.matchingRoom = :roomId")
    List<RoomParticipant> findByMatchingRoom(@Param("roomId") Integer roomId);

    @Query("SELECT COUNT(p) > 0 FROM RoomParticipant p WHERE p.matchingRoom = :roomId AND p.userId = :userId")
    boolean existsByMatchingRoomAndUserId(@Param("roomId") Integer roomId, @Param("userId") String userId);

    @Query("SELECT p FROM RoomParticipant p WHERE p.matchingRoom = :roomId AND p.userId = :userId")
    Optional<RoomParticipant> findByMatchingRoomAndUserId(@Param("roomId") Integer roomId, @Param("userId") String userId);

    // 컨트롤러 호환성을 위한 편의 메소드들 (Long → Integer 변환)
    default List<RoomParticipant> findByMatchingRoomId(Long matchingRoomId) {
        return findByMatchingRoom(matchingRoomId.intValue());
    }

    default boolean existsByMatchingRoomIdAndUserId(Long matchingRoomId, String userId) {
        return existsByMatchingRoomAndUserId(matchingRoomId.intValue(), userId);
    }

    default Optional<RoomParticipant> findByMatchingRoomIdAndUserId(Long matchingRoomId, String userId) {
        return findByMatchingRoomAndUserId(matchingRoomId.intValue(), userId);
    }
}