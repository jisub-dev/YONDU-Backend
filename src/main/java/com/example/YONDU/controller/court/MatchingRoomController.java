package com.example.YONDU.controller.court;

import com.example.YONDU.dto.court.*;
import com.example.YONDU.entity.court.MatchingRoom;
import com.example.YONDU.entity.court.RoomParticipant;
import com.example.YONDU.repository.court.CourtRepository;
import com.example.YONDU.repository.court.MatchingRoomRepository;
import com.example.YONDU.repository.court.RoomParticipantRepository;
import com.example.YONDU.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/court")
public class MatchingRoomController {

    private final MatchingRoomRepository roomRepo;
    private final RoomParticipantRepository participantRepo;
    private final CourtRepository courtRepo;

    public MatchingRoomController(MatchingRoomRepository roomRepo,
                                  RoomParticipantRepository participantRepo,
                                  CourtRepository courtRepo) {
        this.roomRepo = roomRepo;
        this.participantRepo = participantRepo;
        this.courtRepo = courtRepo;
    }

    /**
     * GET /api/court
     * 현재 사용자 NTRP 기반 매칭 룸 필터링
     */
    @GetMapping
    public ResponseEntity<?> getCourtList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        BigDecimal ntrp = new BigDecimal(userDetails.getUser().getNtrp());

        List<MatchingRoomDto> result = roomRepo.findByNtrpMinLessThanEqualAndNtrpMaxGreaterThanEqual(ntrp, ntrp)
                .stream()
                .map(MatchingRoomDto::from)
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "success", true,
                "rooms", result
        ));
    }

    /**
     * POST /api/court
     * 매칭 룸 생성
     */
    @PostMapping
    public ResponseEntity<?> createRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestBody CreateMatchingRoomRequestDto req) {
        if (req.getCourtId() == null || req.getNtrpMin() == null || req.getNtrpMax() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "success", false,
                    "message", "필수 값이 누락되었습니다"
            ));
        }

        MatchingRoom room = new MatchingRoom();
        room.setHostId(userDetails.getUser().getIdentifier());
        room.setCourtId(req.getCourtId());
        room.setType(req.getType());
        room.setNtrpMin(req.getNtrpMin());
        room.setNtrpMax(req.getNtrpMax());
        room.setStartTime(req.getStartTime());
        room.setEndTime(req.getEndTime());
        room.setOnOffFlag(req.getOnOffFlag());
        room.setCreatedDate(LocalDateTime.now());
        room.setUpdatedDate(LocalDateTime.now());

        roomRepo.save(room);

        RoomParticipant me = new RoomParticipant();
        me.setMatchingRoomId(room.getId());
        me.setUserId(userDetails.getUser().getIdentifier());
        me.setStatus("approved");
        me.setJoinedAt(LocalDateTime.now());
        participantRepo.save(me);

        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "success", true,
                "message", "매칭 룸 생성 완료"
        ));
    }

    /**
     * PATCH /api/court
     * 매칭 룸 정보 수정 (호스트만 가능)
     */
    @PatchMapping
    public ResponseEntity<?> updateRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestBody UpdateMatchingRoomRequestDto req) {
        Optional<MatchingRoom> opt = roomRepo.findById(req.getMatchingRoomId());
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "success", false,
                    "message", "매칭 룸을 찾을 수 없습니다"
            ));
        }

        MatchingRoom room = opt.get();
        if (!room.getHostId().equals(userDetails.getUser().getIdentifier())) {
            return ResponseEntity.status(403).body(Map.of(
                    "status", 403,
                    "success", false,
                    "message", "방장만 수정할 수 있습니다"
            ));
        }

        room.setType(req.getType());
        room.setNtrpMin(req.getNtrpMin());
        room.setNtrpMax(req.getNtrpMax());
        room.setStartTime(req.getStartTime());
        room.setEndTime(req.getEndTime());
        room.setOnOffFlag(req.getOnOffFlag());
        room.setUpdatedDate(LocalDateTime.now());
        roomRepo.save(room);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "success", true,
                "message", "매칭 룸 수정 완료"
        ));
    }

    /**
     * POST /api/court/entrance
     * 매칭 룸 입장
     */
    @PostMapping("/entrance")
    public ResponseEntity<?> enterRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestBody MatchingRoomEntranceDto req) {
        String userId = userDetails.getUser().getIdentifier();
        Long roomId = req.getMatchingRoomId();

        if (req == null || req.getMatchingRoomId() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "success", false,
                    "message", "요청이 잘못되었습니다"
            ));
        }


        if (!roomRepo.existsById(roomId)) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "success", false,
                    "message", "존재하지 않는 매칭 룸입니다"
            ));
        }

        if (participantRepo.existsByMatchingRoomIdAndUserId(roomId, userId)) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", 400,
                    "success", false,
                    "message", "이미 입장한 사용자입니다"
            ));
        }

        RoomParticipant p = new RoomParticipant();
        p.setMatchingRoomId(roomId);
        p.setUserId(userId);
        p.setStatus("approved");
        p.setJoinedAt(LocalDateTime.now());
        participantRepo.save(p);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "success", true,
                "message", "입장 완료"
        ));
    }

    /**
     * POST /api/court/exit
     * 매칭 룸 퇴장
     */
    @PostMapping("/exit")
    public ResponseEntity<?> exitRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestBody MatchingRoomEntranceDto req) {
        String userId = userDetails.getUser().getIdentifier();
        Long roomId = req.getMatchingRoomId();

        RoomParticipant participant = participantRepo.findByMatchingRoomIdAndUserId(roomId, userId)
                .orElse(null);

        if (participant == null) {
            return ResponseEntity.status(403).body(Map.of(
                    "status", 403,
                    "success", false,
                    "message", "참여 중인 유저가 아닙니다"
            ));
        }

        participantRepo.delete(participant);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "success", true,
                "message", "퇴장 완료"
        ));
    }
}
