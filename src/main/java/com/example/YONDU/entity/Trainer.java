package com.example.YONDU.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trainer")
@Getter
@Setter
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Integer trainerId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "assignable", nullable = false)
    private Boolean assignable = true;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;
}