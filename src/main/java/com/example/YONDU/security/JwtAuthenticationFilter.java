package com.example.YONDU.security;

import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.repository.UserRepository;
import com.example.YONDU.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String identifier = jwtService.extractIdentifier(token);

            // 토큰 유효성 검사
            if (!jwtService.validateToken(token)) {
                sendJsonResponse(response, 999, Map.of(
                        "success", false,
                        "message", "Access token has expired or is invalid"
                ));
                return;
            }

            // 사용자 인증 처리
            if (identifier != null) {
                Optional<UserEntity> userOptional = userRepository.findById(identifier);
                if (userOptional.isPresent()) {
                    UserEntity user = userOptional.get();

                    // 정지된 사용자일 경우 권한 없음 처리
                    if (user.isBanned()) {
                        sendJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, Map.of(
                                "success", false,
                                "message", "User is banned"
                        ));
                        return;
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, null
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    sendJsonResponse(response, HttpServletResponse.SC_NOT_FOUND, Map.of(
                            "success", false,
                            "message", "User not found"
                    ));
                    return;
                }
            } else {
                sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, Map.of(
                        "success", false,
                        "message", "Unauthorized: Invalid identifier"
                ));
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Internal server error"
            ));
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int status, Map<String, Object> body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(ResponseEntity.status(status).body(body).getBody().toString());
        writer.flush();
    }
}
