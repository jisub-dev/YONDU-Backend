package com.example.YONDU.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // [1] 기존 상수 + role 포함용 SECRET_KEY
    private static final String SECRET_KEY = "your-secure-secret-key-your-secure-secret-key";
    private static final String REFRESH_SECRET_KEY = "your-refresh-token-secret-key-your-refresh-token-secret-key";

    // Access Token 만료 (1시간), Refresh Token 만료 (7일)
    private static final long EXPIRATION_TIME = 3600000;         // 1시간 (60*60*1000)
    private static final long REFRESH_EXPIRATION_TIME = 604800000; // 7일 (24*60*60*1000 * 7)

    // =========================================================================
    //  A) 기존 방식: (1) boolean validateToken, (2) generateToken(String email)
    // =========================================================================

    /**
     * 기존 validateToken() - 유효성만 체크하여 true/false 반환
     */
    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwt); // 파싱 성공하면 유효
            return true;
        } catch (ExpiredJwtException | JwtException e) {
            return false;
        }
    }

    /**
     * 기존 generateToken(String email) - role 없이 subject만
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 기존 Refresh Token 생성
     */
    public String generateRefreshToken(String identifier) {
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 기존 refreshToken() - Access Token 재발급
     * (role 없이, 단순히 subject만 뽑아서 generateToken 재발급)
     */
    public String refreshToken(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            // subject(식별자)만 다시 넣어 새 토큰 발급
            return generateToken(claims.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }

    // =========================================================================
    //  B) 새 방식: (1) generateToken(identifier, role), (2) validateToken(...) → Claims
    // =========================================================================

    /**
     * role(ADMIN, MEMBER 등)까지 클레임에 넣고 싶은 경우
     */
    public String generateToken(String identifier, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(identifier)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    /**
     * JWT 파싱 후 Claims 반환 (role 등 여러 필드가 필요할 때)
     */
    public Claims validateAndGetClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
