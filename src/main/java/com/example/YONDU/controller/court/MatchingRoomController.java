package com.example.YONDU.controller.court;

import com.example.YONDU.dto.court.*;
import com.example.YONDU.entity.court.Court;
import com.example.YONDU.entity.court.MatchingRoom;
import com.example.YONDU.entity.court.RoomParticipant;
import com.example.YONDU.repository.court.CourtRepository;
import com.example.YONDU.repository.court.MatchingRoomRepository;
import com.example.YONDU.repository.court.RoomParticipantRepository;
import com.example.YONDU.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 페이지네이션과 참여자 정보가 포함된 매칭 룸 목록 조회
     */
    @GetMapping
    public ResponseEntity<?> getCourtList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 페이지는 1부터 시작하지만 내부적으로는 0부터 시작
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        BigDecimal userNtrp = new BigDecimal(userDetails.getUser().getNtrp());

        // 모든 방을 페이지네이션으로 조회 (NTRP 필터링은 나중에 적용 가능)
        Page<MatchingRoom> roomPage = roomRepo.findAll(pageable);

        // 각 방의 참여자 정보를 포함한 DTO로 변환
        List<MatchingRoomDetailDto> roomDetails = roomPage.getContent().stream()
                .map(this::convertToDetailDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "success", true,
                "rooms", roomDetails,
                "currentPage", page,
                "totalPages", roomPage.getTotalPages(),
                "totalElements", roomPage.getTotalElements(),
                "hasNext", roomPage.hasNext(),
                "hasPrevious", roomPage.hasPrevious()
        ));
    }

    /**
     * MatchingRoom을 MatchingRoomDetailDto로 변환 (참여자 정보 포함)
     */
    private MatchingRoomDetailDto convertToDetailDto(MatchingRoom room) {
        // 참여자 ID 목록 조회 - RoomParticipant 테이블의 구조상 복합키로 되어있어서 수정 필요
        List<String> participantIds = participantRepo.findByMatchingRoomId(room.getId())
                .stream()
                .map(RoomParticipant::getUserId)
                .collect(Collectors.toList());

        // 실제로는 User 테이블에서 이름을 조회해야 하지만,
        // 현재는 간단히 userId를 그대로 사용 (추후 User 조회로 변경 가능)
        List<String> participantNames = participantIds; // 임시로 ID 사용

        // 코트 정보 조회 - Court 엔티티에는 지점명이 없으므로 info 필드 사용
        String branchName = "";
        String courtInfo = "";
        if (room.getCourtId() != null) {
            Optional<Court> court = courtRepo.findById(room.getCourtId());
            if (court.isPresent()) {
                branchName = court.get().getType(); // type을 지점명으로 임시 사용
                courtInfo = court.get().getInfo();
            }
        }

        // 세부 설명 생성 (기존 정보 활용)
        String detail = generateRoomDetail(room, courtInfo);

        return MatchingRoomDetailDto.builder()
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
                .participants(participantNames)
                .branchName(branchName)
                .roomName(room.getType()) // type을 roomName으로 활용
                .detail(detail)
                .build();
    }

    /**
     * 기존 정보를 활용하여 세부 설명 생성
     */
    private String generateRoomDetail(MatchingRoom room, String courtInfo) {
        StringBuilder detail = new StringBuilder();

        detail.append("NTRP ").append(room.getNtrpMin()).append(" ~ ").append(room.getNtrpMax());

        if (room.getOnOffFlag() != null) {
            detail.append(" | ").append(room.getOnOffFlag() ? "온라인" : "오프라인");
        }

        if (courtInfo != null && !courtInfo.isEmpty()) {
            detail.append(" | ").append(courtInfo);
        }

        if (room.getStartTime() != null && room.getEndTime() != null) {
            detail.append(" | ").append(room.getStartTime().toLocalDate())
                    .append(" ").append(room.getStartTime().toLocalTime())
                    .append(" ~ ").append(room.getEndTime().toLocalTime());
        }

        return detail.toString();
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

        // 방장을 자동으로 참여자로 추가
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

        // 기존 필드들 업데이트 (null 체크 추가)
        if (req.getType() != null) {
            room.setType(req.getType());
        }
        if (req.getNtrpMin() != null) {
            room.setNtrpMin(req.getNtrpMin());
        }
        if (req.getNtrpMax() != null) {
            room.setNtrpMax(req.getNtrpMax());
        }
        if (req.getStartTime() != null) {
            room.setStartTime(req.getStartTime());
        }
        if (req.getEndTime() != null) {
            room.setEndTime(req.getEndTime());
        }
        if (req.getOnOffFlag() != null) {
            room.setOnOffFlag(req.getOnOffFlag());
        }

        room.setUpdatedDate(LocalDateTime.now());
        roomRepo.save(room);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "success", true,
                "message", "매칭 룸 수정 완료",
                "room", convertToDetailDto(room)
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