package com.example.YONDU.entity.pt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "pt_session")
@Getter
@Setter
public class PtSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sessionId")
    private Integer sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packageId", nullable = false)
    private PtPackage ptPackage;

    @Column(name = "sessionDate", nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private SessionStatus status;

    public enum SessionStatus {
        예약, 확인, 완료, 취소
    }
}