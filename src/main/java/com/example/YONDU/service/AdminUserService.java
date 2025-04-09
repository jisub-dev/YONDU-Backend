//package com.example.YONDU.service;
//
//import com.example.YONDU.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.stream.Collectors;
//
//@Service
//public class AdminUserService {
//
//    private final UserRepository userRepository;
//    // 필요 시, PasswordEncoder 등 의존성 주입
//
//    public AdminUserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public List<UserDto> getAllUsers() {
//        // DB에서 유저 목록 조회
//        List<User> users = userRepository.findAll();
//        // Entity -> DTO 변환 후 반환
//        return users.stream().map(UserDto::from).collect(Collectors.toList());
//    }
//
//    @Transactional
//    public void updateUserInfo(String identifier, ModifyInfoRequest modifyRequest) {
//        User user = userRepository.findByIdentifier(identifier)
//                .orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다"));
//
//        // RequestBody에 포함된 필드만 변경
//        if (modifyRequest.getName() != null) {
//            user.setName(modifyRequest.getName());
//        }
//        if (modifyRequest.getPhone() != null) {
//            user.setPhone(modifyRequest.getPhone());
//        }
//        if (modifyRequest.getRole() != null) {
//            user.setRole(modifyRequest.getRole());  // "TRAINER" 등
//        }
//        // 비밀번호 변경 시 암호화
//        if (modifyRequest.getPassword() != null) {
//            // user.setPassword(passwordEncoder.encode(modifyRequest.getPassword()));
//        }
//        // 기타 필드들 수정 처리
//        // ...
//        userRepository.save(user);
//    }
//
//    @Transactional
//    public void deleteUser(String identifier) {
//        User user = userRepository.findByIdentifier(identifier)
//                .orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다"));
//        userRepository.delete(user);
//    }
//}
