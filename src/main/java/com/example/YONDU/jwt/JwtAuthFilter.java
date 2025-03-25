package com.example.YONDU.jwt;

import com.example.YONDU.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter extends jakarta.servlet.Filter {

    private final JwtService jwtService;

    @Override
    public void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 'Bearer ' 제거

            try {
                // JWT 검증 & Claims 파싱
                Claims claims = jwtService.validateToken(token);

                // subject = 사용자 식별자
                String identifier = claims.getSubject();
                // role 클레임 예: "ADMIN", "MEMBER"
                String role = (String) claims.get("role");

                if (StringUtils.hasText(identifier) && StringUtils.hasText(role)) {
                    // 스프링 시큐리티에선 ROLE_ 접두어가 필요
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(identifier, null, List.of(authority));

                    // SecurityContextHolder 에 인증 정보 세팅
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // 토큰이 만료 or 위조 등 → 그냥 인증 실패로 간주
            }
        }

        chain.doFilter(request, response);
    }
}
