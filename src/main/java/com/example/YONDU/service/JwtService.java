
package com.example.YONDU.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET_KEY = "your-secure-secret-key-your-secure-secret-key";
    private static final String REFRESH_SECRET_KEY = "your-refresh-token-secret-key-your-refresh-token-secret-key";
    private static final long EXPIRATION_TIME = 3600000; // 1시간
    private static final long REFRESH_EXPIRATION_TIME = 1209600000; // 14일



    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException e) {
            return false;
        }
    }

    public String refreshToken(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return generateToken(claims.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(String identifier) {
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱
            return true; // 파싱 성공 시 유효함
        } catch (ExpiredJwtException e) {
            return false; // 만료됨
        } catch (JwtException e) {
            return false; // 위조되었거나 형식 오류
        }
    }

    // 토큰에서 사용자 식별자 추출
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // setSubject에 넣어둔 값이 곧 사용자 식별자
        } catch (JwtException e) {
            return null;
        }
    }
}
