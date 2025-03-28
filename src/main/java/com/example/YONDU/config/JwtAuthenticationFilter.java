package com.example.YONDU.config;

import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.UserRepository;
import com.example.YONDU.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * 매 요청마다 Authorization 헤더에 담긴 토큰을 검증 & 인증정보를 SecurityContext에 설정.
 * 이렇게 하면 Controller 단에서 별도로 토큰 파싱 로직을 중복 작성할 필요 없이,
 * securityContext에 담긴 인증 정보를 꺼내 쓸 수 있습니다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 액세스 토큰 검증
            if (jwtService.validateToken(token)) {
                // 토큰에서 식별자 추출
                String identifier = jwtService.extractIdentifier(token);
                if (identifier != null) {
                    // DB에서 사용자 조회
                    Optional<UserEntity> userOptional = userRepository.findById(identifier);
                    if (userOptional.isPresent()) {
                        UserEntity user = userOptional.get();
                        // 스프링 시큐리티가 요구하는 형식의 Authentication 객체 생성
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        user, // Principal(주체)
                                        null, // Credentials(비밀번호 등은 null 처리)
                                        Collections.emptyList() // 권한(roles) 필요 시 세팅
                                );
                        // SecurityContext에 인증정보 등록
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
