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

    @Column(name = "memberNo", length = 20, unique = true)
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

    @Column(name = "career", length = 20, nullable = false)
    private String career;

    @Column(name = "ntrp", length = 15, nullable = false)
    private String ntrp;

    @Column(name = "refundAccount", length = 255, nullable = false)
    private String refundAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refundBank", nullable = false)
    private Bank refundBank;

    @Enumerated(EnumType.STRING)
    @Column(name = "receiptInfo")
    private ReceiptBoolean receiptInfo;

    @Column(name = "trainerId")
    private String trainerId;

    @Column(name = "banned", nullable = false)
    private boolean banned;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "refreshToken", length = 500)
    private String refreshToken;
}