package com.example.YONDU.entity.court;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "matching_room")
@Getter @Setter
public class MatchingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "host_id")
    private String hostId;

    @Column(name = "court_id")
    private Long courtId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "type")
    private String type;  // 예: "2인 단식", "4인 복식"

    @Column(name = "on_off_flag")
    private Boolean onOffFlag;

    @Column(name = "ntrp_min", precision = 2, scale = 1)
    private BigDecimal ntrpMin;

    @Column(name = "ntrp_max", precision = 2, scale = 1)
    private BigDecimal ntrpMax;

    @Column(name = "participate_id")
    private Integer participateId; // 사용 여부에 따라 생략 가능

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
