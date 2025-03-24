package com.example.YONDU.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;          // 임시 토큰 문자열
    private String userIdentifier; // 어떤 user(identifier)에 해당하는 토큰인지
    private LocalDateTime expiredAt;
    private boolean used;

    public boolean isExpired() {
        // 이미 used == true이거나 expiredAt 지났으면 만료
        return used || (expiredAt != null && LocalDateTime.now().isAfter(expiredAt));
    }

    public void markAsUsed() {
        this.used = true;
    }
}
