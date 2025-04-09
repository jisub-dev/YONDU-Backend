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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


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
                .csrf(csrf -> csrf.disable())// CSRF 비활성(필요 시 활성화)
                .cors(cors -> {
                    // 1. CORS 정책 생성
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin("*");// 허용할 도메인 //FIXME: 도메인 서버 IP로 변경, 예시:(http://localhost:3000)
                    config.addAllowedMethod("*");// 허용할 HTTP 메서드
                    config.addAllowedHeader("*");// 허용할 헤더
                    config.setAllowCredentials(false);// 인증 정보 포함 여부 //FIXME: 세션 또는 쿠키를 쓰지 않으면 false로 , 원래 true였음

                    // 2. URL별로 어떤 CORS 정책을 적용할 것인지 source에 등록
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", config);// 모든 경로에 대해서 위 세팅을 적용

                    // 3. 최종적으로 Security에 CORS 소스를 설정
                    cors.configurationSource(source);
                })
                .authorizeHttpRequests(auth -> auth
                        // 로그인/회원가입/토큰재발급 등은 토큰 없이 호출 가능
                        .requestMatchers("/api/auth/**", "/api/tokens/refresh").permitAll()
                        // 스웨거 관련 등도 토큰 없이 호출 가능
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/openapi/**").permitAll()
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