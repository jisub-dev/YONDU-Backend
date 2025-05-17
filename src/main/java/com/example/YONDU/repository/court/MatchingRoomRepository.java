package com.example.YONDU.repository.court;

import com.example.YONDU.entity.court.MatchingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface MatchingRoomRepository extends JpaRepository<MatchingRoom, Long> {
    List<MatchingRoom> findByNtrpMinLessThanEqualAndNtrpMaxGreaterThanEqual(BigDecimal myNtrp1, BigDecimal myNtrp2);
}
