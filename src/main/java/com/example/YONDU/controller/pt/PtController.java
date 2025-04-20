package com.example.YONDU.controller.pt;

import com.example.YONDU.dto.pt.*;
import com.example.YONDU.entity.Role;
import com.example.YONDU.entity.pt.PtSession;
import com.example.YONDU.repository.pt.*;
import com.example.YONDU.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/PT/mypt")
public class PtController {

    private final PtPackageRepository pkgRepo;
    private final PtSessionRepository sessionRepo;

    public PtController(PtPackageRepository pkgRepo, PtSessionRepository sessionRepo) {
        this.pkgRepo = pkgRepo;
        this.sessionRepo = sessionRepo;
    }

    /**
     * 9) 회원 자신의 PT 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String,Object>> getMyPT(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String loginId = userDetails.getUser().getIdentifier();
            Role role   = userDetails.getUser().getRole();

            List<PtSessionDto> pts;
            if (role == Role.MEMBER) {
                // 회원이라면 본인이 구매한 패키지만
                pts = pkgRepo.findByMemberIdentifier(loginId).stream()
                        .map(PtSessionDto::fromPackage)
                        .toList();

            } else if (role == Role.TRAINER) {
                // 트레이너라면 본인이 담당 중인 회원들의 패키지
                pts = pkgRepo.findByTrainerIdentifier(loginId).stream()
                        .map(PtSessionDto::fromPackage)
                        .toList();

            } else {
                // 매니저 등 그 외 권한은 전체 조회 혹은 빈 리스트
                pts = pkgRepo.findAll().stream()
                        .map(PtSessionDto::fromPackage)
                        .toList();
            }

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "success", true,
                    "pts", pts
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }

    /**
     * 9-1a) 특정 PT 세션 상세 조회
     */
    @GetMapping("/{ptSessionId}")
    public ResponseEntity<Map<String,Object>> getMyPTDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Integer ptSessionId) {
        try {
            Optional<PtSession> opt = sessionRepo.findById(ptSessionId);
            if(opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status",404,
                        "success",false,
                        "message","해당 PT 세션을 찾을 수 없습니다."
                ));
            }
            PtSession session = opt.get();
            // 소유권 체크
            String loginId = userDetails.getUser().getIdentifier();
            Role role    = userDetails.getUser().getRole();

            boolean allowed;
            if (role == Role.MEMBER) {
                allowed = session.getPtPackage().getMemberIdentifier().equals(loginId);
            } else if (role == Role.TRAINER) {
                allowed = session.getPtPackage().getTrainerIdentifier().equals(loginId);
            } else {
                // MANAGER 등 기타 권한은 전부 허용하거나 별도 로직
                allowed = true;
            }

            if (!allowed) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "status", 403,
                        "success", false,
                        "message", "접근 권한이 없습니다."
                ));
            }
            PtDetailDto detail = PtDetailDto.fromSession(session);
            return ResponseEntity.ok(Map.of(
                    "status",200,
                    "success",true,
                    "ptDetail",detail
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }

    /**
     * 9-1b) 특정 PT 세션 수정 요청 처리
     */
    @PatchMapping("/{ptSessionId}")
    @Transactional
    public ResponseEntity<Map<String,Object>> patchMyPTDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer ptSessionId,
            @RequestBody PtUpdateRequestDto req) {
        try {
            // 1) 스케줄 조회
            Optional<PtSession> opt = sessionRepo.findById(req.getScheduleId());
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status", 404,
                        "success", false,
                        "message", "해당 스케줄을 찾을 수 없습니다."
                ));
            }
            PtSession schedule = opt.get();

            // 2) 사용자 정보
            String loginId = userDetails.getUser().getIdentifier();
            Role role      = userDetails.getUser().getRole();
            PtSession.SessionStatus current = schedule.getStatus();
            String action = req.getAction();

            // 3) 권한 + 상태 검증
            if (role == Role.TRAINER) {
                // 트레이너는 '예약' 상태만 확인/취소 가능
                if (current != PtSession.SessionStatus.예약 ||
                        (!action.equals("REQUEST_COMPLETE") && !action.equals("CANCEL"))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                            "status", 403,
                            "success", false,
                            "message", "트레이너는 예약 상태에서만 확인(REQUEST_COMPLETE) 또는 취소(CANCEL)할 수 있습니다."
                    ));
                }
            }
            else if (role == Role.MEMBER) {
                // 회원은 '확인' 상태만 완료 가능
                if (current != PtSession.SessionStatus.확인 ||
                        !action.equals("COMPLETE")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                            "status", 403,
                            "success", false,
                            "message", "회원은 확인 상태에서만 완료(COMPLETE)할 수 있습니다."
                    ));
                }
            }
            else if(role == Role.MANAGER){
            }
            else{
                // 혹시나 없는 경우가 있을까봐 넣어둠
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "status", 403,
                        "success", false,
                        "message", "권한이 없습니다."
                ));
            }

            // 4) 상태 변경
            switch (action) {
                case "REQUEST_COMPLETE": // 트레이너가 확인으로
                    schedule.setStatus(PtSession.SessionStatus.확인);
                    break;
                case "CANCEL":           // 트레이너가 취소로
                    schedule.setStatus(PtSession.SessionStatus.취소);
                    break;
                case "COMPLETE":         // 회원이 완료로
                    schedule.setStatus(PtSession.SessionStatus.완료);
                    break;
                default:
                    // (필요시 UPDATE_SCHEDULE 등 추가)
                    break;
            }

            // 5) 저장 및 응답
            sessionRepo.save(schedule);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "success", true,
                    "message", "PT 세션 수정 완료"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }
}
