package com.example.YONDU.config;

import com.example.YONDU.repository.UserRepository;
import com.example.YONDU.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 비밀번호 암호화용 Bean만 있던 부분을 확장,
     * Spring Security FilterChain을 정의해 JWTAuthenticationFilter를 등록합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Security 설정
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성(필요 시 활성화)
                .authorizeHttpRequests(auth -> auth
                        // 로그인/회원가입/토큰재발급 등은 토큰 없이 호출 가능
                        .requestMatchers("/api/auth/**", "/api/tokens/refresh").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // 커스텀 JwtAuthenticationFilter 등록
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 세션 사용 안 함 (JWT만 쓰는 경우)
        http.sessionManagement(session -> session.disable());

        return http.build();
    }

    /**
     * JWTAuthenticationFilter를 Bean으로 등록
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }
}