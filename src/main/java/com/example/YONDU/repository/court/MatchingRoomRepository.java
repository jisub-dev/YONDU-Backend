package com.example.YONDU.repository.court;

import com.example.YONDU.entity.court.MatchingRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.List;

public interface MatchingRoomRepository extends JpaRepository<MatchingRoom, Long> {

    // 기존 메소드들 유지
    List<MatchingRoom> findByNtrpMinLessThanEqualAndNtrpMaxGreaterThanEqual(
            BigDecimal ntrpMin, BigDecimal ntrpMax);

    // 페이지네이션 지원 메소드 (추후 NTRP 필터링 추가 가능)
    Page<MatchingRoom> findByNtrpMinLessThanEqualAndNtrpMaxGreaterThanEqual(
            BigDecimal ntrpMin, BigDecimal ntrpMax, Pageable pageable);
}