package com.example.YONDU.entity.court;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "court")
@Getter @Setter
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "court_id")
    private Long courtId;

    @Column(name = "type")
    private String type;  // enum('클레이', '잔디', '흙', '하드')

    @Column(name = "info")
    private String info;
}
