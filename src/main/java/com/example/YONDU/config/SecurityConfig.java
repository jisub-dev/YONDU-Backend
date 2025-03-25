package com.example.YONDU.config;

import com.example.YONDU.jwt.JwtAuthFilter;
import com.example.YONDU.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // JWT 환경에서 사용한다면, CSRF 비활성화 권장 (폼로그인 안 쓸 경우)
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> {
            // /api/admin/... : ADMIN 권한 필요
            auth.requestMatchers("/api/admin/**").hasRole("ADMIN");
            // 나머지 경로는 일단 모두 허용(원하는 대로 수정 가능)
            auth.anyRequest().permitAll();
        });

        // JWT 필터 추가
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        // 로그인/로그아웃 설정 (필요 시 커스텀)
        http.formLogin(Customizer.withDefaults());
        http.logout(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        // 아래에서 구현할 JwtAuthFilter 에 jwtService 주입
        return new JwtAuthFilter(jwtService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 매니저 (간단 예시)
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager();
    }
}
