//package com.example.YONDU.controller;
//
//import com.example.YONDU.dto.LogginDto;
//import com.example.YONDU.service.AdminUserService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/admin/modify-info")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminUserController {
//
//    private final AdminUserService adminUserService;
//
//    public AdminUserController(AdminUserService adminUserService) {
//        this.adminUserService = adminUserService;
//    }
//
//    // GET /api/admin/modify-info
//    @GetMapping
//    public ResponseEntity<?> getAllUsers() {
//        // 관리자 권한 체크는 Security 설정으로 이미 처리됨
//        List<LogginDto> userList = adminUserService.getAllUsers();
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", 200);
//        response.put("success", true);
//        response.put("data", userList);
//        return ResponseEntity.ok(response);
//    }
//
//    // PATCH /api/admin/modify-info/{identifier}
//    @PatchMapping("/{identifier}")
//    public ResponseEntity<?> updateUserInfo(
//            @PathVariable("identifier") String identifier,
//            @RequestBody ModifyInfoRequest modifyRequest
//    ) {
//        adminUserService.updateUserInfo(identifier, modifyRequest);
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", 200);
//        response.put("success", true);
//        response.put("message", "수정 성공");
//        return ResponseEntity.ok(response);
//    }
//
//    // DELETE /api/admin/modify-info/{identifier}
//    @DeleteMapping("/{identifier}")
//    public ResponseEntity<?> deleteUser(@PathVariable("identifier") String identifier) {
//        adminUserService.deleteUser(identifier);
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", 200);
//        response.put("success", true);
//        response.put("message", "사용자 삭제 완료");
//        return ResponseEntity.ok(response);
//    }
//
//}
//
