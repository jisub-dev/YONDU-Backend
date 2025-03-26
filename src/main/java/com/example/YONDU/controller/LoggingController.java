package com.example.YONDU.controller;

import com.example.YONDU.dto.LogginDto;
import com.example.YONDU.dto.TrainerInfoDto;
import com.example.YONDU.entity.Bank;
import com.example.YONDU.entity.Role;
import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.UserRepository;
import com.example.YONDU.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class LoggingController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoggingController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> loginRequest) {
        try {
            String identifier = loginRequest.get("identifier");
            String password = loginRequest.get("password");

            // 이메일 또는 비밀번호 누락 시 400 Bad Request 반환
            if (identifier == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Email and password are required"
                ));
            }

            Optional<UserEntity> userOptional = userRepository.findById(identifier);

            if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Invalid identifier or password"
                ));
            }
            UserEntity user = userOptional.get();

            if (user.isBanned()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "success", false,
                        "message", "User is banned"
                ));
            }
            // JWT 액세스 토큰 및 리프레시 토큰 생성
            String token = jwtService.generateToken(user.getIdentifier(), user.getRole());
            String refreshToken = jwtService.generateRefreshToken(user.getIdentifier());

            // 리프레시 토큰을 저장 후 업데이트
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", token,
                    "RT", refreshToken,
                    "user", new LogginDto(userOptional.get())
            ));
        } catch (Exception e) {
            //서버 내부 오류 (500 INTERNAL_SERVER_ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }
    @DeleteMapping("/signin")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String, String> logoutRequest) {
        try {
            String identifier = logoutRequest.get("identifier");

            // 이메일 또는 비밀번호 누락 시 400 Bad Request 반환
            if (identifier == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Email are required"
                ));
            }

            Optional<UserEntity> userOptional = userRepository.findById(identifier);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Invalid identifier"
                ));
            }
            UserEntity user = userOptional.get();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "logout",
                    "user", new LogginDto(userOptional.get())
            ));
        } catch (Exception e) {
            //서버 내부 오류 (500 INTERNAL_SERVER_ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody Map<String, String> signUpRequest) {
        try {
            String identifier = signUpRequest.get("identifier");
            String password = signUpRequest.get("password");
            String name = signUpRequest.get("name");
            String gender = signUpRequest.get("gender");
            String phone = signUpRequest.get("phone");
            String branch = signUpRequest.get("branch");
            String birth = signUpRequest.get("birth");
            String career = signUpRequest.get("career");
            String ntrp = signUpRequest.get("ntrp");
            String refundAccount = signUpRequest.get("refundAccount");
            String refundBank = signUpRequest.get("refundBank");
            String receiptInfo = signUpRequest.get("receiptInfo");
            String trainerId = signUpRequest.containsKey("trainerId") ? signUpRequest.get("trainerId") : null;



            // 필수 필드 확인 (400 Bad Request)
            if (identifier == null || password == null || name == null || phone == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Identifier, password, name, and phone are required"
                ));
            }

            // 이메일 중복 확인 (409 Conflict)
            if (userRepository.existsByIdentifier(identifier)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "success", false,
                        "message", "Identifier is already in use"
                ));
            }

            if (userRepository.existsByPhone(phone)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "success", false,
                        "message", "phonenumber is already in use"
                ));
            }

            // UserEntity 생성 및 저장
            UserEntity newUser = new UserEntity();
            newUser.setIdentifier(identifier);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setName(name);
            newUser.setGender(gender);
            newUser.setPhone(phone);
            newUser.setBranch(branch);
            newUser.setBirth(birth);
            newUser.setCareer(career);
            newUser.setNtrp(ntrp);
            newUser.setRefundAccount(refundAccount);
            Bank selectedBank = Bank.fromString(refundBank); // 한글 은행명을 Enum으로 변환
            newUser.setRefundBank(selectedBank);
            newUser.setReceiptInfo(receiptInfo);
            newUser.setTrainerId(trainerId);
            newUser.setBanned(false);
            newUser.setRole(Role.MEMBER);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            newUser.setRefreshToken(null);

            System.out.println("여기까지 돌아감");
            UserEntity savedUser = userRepository.save(newUser);

            // 성공 응답 (201 Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "User registered successfully",
                    "user", new LogginDto(savedUser)
            ));
        } catch (Exception e) {
            //서버 내부 오류 (500 INTERNAL_SERVER_ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }
    @GetMapping("/signup")
    public ResponseEntity<Map<String, Object>> getTrainerList() {
        List<UserEntity> trainers = userRepository.findByRole(Role.TRAINER);

        List<TrainerInfoDto> trainerInfoList = trainers.stream()
                .map(trainer -> new TrainerInfoDto(
                        trainer.getIdentifier(),
                        trainer.getName(),
                        trainer.getBranch()
                ))
                .toList();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Trainer list successfully",
                "trainers", trainerInfoList
        ));
    }
<<<<<<< HEAD
=======

    @PatchMapping("/auth/modify-info")
    public ResponseEntity<Map<String, Object>> modifyInfo(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, String> request
    ) {
        try {
            // 1. 토큰에서 사용자 식별자(identifier)를 추출
            String token = bearerToken.replace("Bearer ", "");
            String identifier = jwtService.extractIdentifier(token);

            // 2. DB에서 사용자 정보를 가져옴
            Optional<UserEntity> userOptional = userRepository.findById(identifier);
            if (userOptional.isEmpty()) {
                // 사용자 정보를 찾지 못한 경우
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "User not found"
                ));
            }

            UserEntity user = userOptional.get();

            // 3. 변경하고자 하는 값이 request 바디에 있으면 해당 필드만 수정
            if (request.containsKey("password")) {
                String rawPassword = request.get("password");
                user.setPassword(passwordEncoder.encode(rawPassword));
            }

            if (request.containsKey("refundBank")) {
                String refundBank = request.get("refundBank");
                Bank selectedBank = Bank.fromString(refundBank); // 문자열 -> Bank enum
                user.setRefundBank(selectedBank);
            }

            if (request.containsKey("refundAccount")) {
                user.setRefundAccount(request.get("refundAccount"));
            }

            if (request.containsKey("receiptInfo")) {
                user.setReceiptInfo(request.get("receiptInfo"));
            }

            if (request.containsKey("phone")) {
                String newPhone = request.get("phone");
                user.setPhone(newPhone);
            }

            // 4. 수정 시간을 갱신(UpdatedAt)
            user.setUpdatedAt(LocalDateTime.now());

            // 5. DB에 저장
            userRepository.save(user);

            // 6. 수정 완료 후 클라이언트에 성공 응답
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User info updated",
                    "user", new LogginDto(user)
            ));

        } catch (Exception e) {
            // 서버에서 예외가 난 경우, 500 에러 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }


>>>>>>> feature/user_auth-RTtkoen-dev-logout&infopatch
}