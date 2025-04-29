package com.example.YONDU.controller.notice;

import com.example.YONDU.dto.notice.NoticeDto;
import com.example.YONDU.repository.notice.NoticeRepository;
import com.example.YONDU.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeRepository noticeRepository;

    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /**
     * 직원 공지사항 조회 API
     */
    @GetMapping("/employee")
    public ResponseEntity<Map<String, Object>> getEmployeeNotices(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // 1. 인증 사용자 확인
            if (userDetails == null || userDetails.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "status", 401,
                        "success", false,
                        "message", "로그인이 필요합니다"
                ));
            }

            // 2. 공지사항 조회
            List<NoticeDto> notices = noticeRepository.findAllByOrderByCreatedAtDesc()
                    .stream()
                    .map(NoticeDto::from)
                    .toList();

            // 3. 빈 리스트인 경우 메시지 처리 (선택적)
            if (notices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                        "status", 200,
                        "success", true,
                        "message", "공지사항이 없습니다",
                        "notices", List.of()
                ));
            }

            // 4. 정상 응답
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "success", true,
                    "notices", notices
            ));

        } catch (Exception e) {
            // 5. 예외 발생 시 서버 오류 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "success", false,
                    "message", "공지사항 조회 중 오류가 발생했습니다",
                    "error", e.getMessage()
            ));
        }
    }
}
