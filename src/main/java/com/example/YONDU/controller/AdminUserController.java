package com.example.YONDU.controller;

import com.example.YONDU.dto.LogginDto;
import com.example.YONDU.entity.Bank;
import com.example.YONDU.entity.ReceiptBoolean;
import com.example.YONDU.entity.Role;
import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/modify-info")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 전체 회원 정보 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "All users fetched",
                    "users", users.stream().map(LogginDto::new).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }

    // 회원 정보 수정
    @PatchMapping("/{identifier}")
    @Transactional
    public ResponseEntity<Map<String, Object>> modifyUserInfo(@PathVariable String identifier,
                                                              @RequestBody Map<String, String> updates) {
        try {
            Optional<UserEntity> userOptional = userRepository.findById(identifier);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "User not found"
                ));
            }

            UserEntity user = userOptional.get();

            updates.forEach((key, value) -> {
                switch (key) {
                    case "password" -> user.setPassword(value);
                    case "memberNo" -> user.setMemberNo(value);
                    case "role" -> user.setRole(Role.valueOf(value));
                    case "name" -> user.setName(value);
                    case "phone" -> user.setPhone(value);
                    case "branch" -> user.setBranch(value);
                    case "career" -> user.setCareer(value);
                    case "ntrp" -> user.setNtrp(value);
                    case "refundAccount" -> user.setRefundAccount(value);
                    case "refundBank" -> user.setRefundBank(Bank.fromString(value));
                    case "receiptInfo" -> user.setReceiptInfo(ReceiptBoolean.valueOf(value));
                    case "receiptType" -> user.setReceiptType(value);
                    case "receiptNumber" -> user.setReceiptNumber(value);
                    case "trainerId" -> user.setTrainerId(value);
                    case "banned" -> user.setBanned(Boolean.parseBoolean(value));
                }
            });

            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User info updated",
                    "user", new LogginDto(user)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }

    // 회원 삭제
    @DeleteMapping("/{identifier}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String identifier) {
        try {
            Optional<UserEntity> userOptional = userRepository.findById(identifier);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "User not found"
                ));
            }

            userRepository.deleteById(identifier);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User deleted"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }
}
