package com.example.YONDU.entity.pt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pt_package")
@Getter
@Setter
public class PtPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "packageId")
    private Integer packageId;

    @Column(name = "totalSessions", nullable = false)
    private Integer totalSessions;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "trainerIdentifier", nullable = false, length = 50)
    private String trainerIdentifier;

    @Column(name = "memberIdentifier", nullable = false, length = 50)
    private String memberIdentifier;

    @OneToMany(mappedBy = "ptPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PtSession> sessions;
}