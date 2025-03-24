package com.example.YONDU.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(name = "identifier", length = 50, nullable = false)
    private String identifier;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "member_no", length = 20, unique = true)
    private String memberNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "gender", length = 1, nullable = false)
    private String gender;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "branch", length = 50, nullable = false)
    private String branch;

    @Column(name = "birth", length = 8, nullable = false)
    private String birth;

    @Column(name = "career", length = 4, nullable = false)
    private String career;

    @Column(name = "ntrp", length = 3, nullable = false)
    private String ntrp;

    @Column(name = "refund_account", length = 255, nullable = false)
    private String refundAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_bank", nullable = false)
    private Bank refundBank;

    @Column(name = "receipt_info", length = 255)
    private String receiptInfo;

    @Column(name = "trainer_id")
    private String trainerId;

    @Column(name = "banned", nullable = false)
    private boolean banned;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;
}