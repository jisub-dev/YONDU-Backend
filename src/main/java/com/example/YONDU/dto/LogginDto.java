package com.example.YONDU.dto;

import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.entity.Role;
import com.example.YONDU.entity.Bank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LogginDto {
    private String identifier;
    private String name;
    private Role role;
    private String gender;
    private String phone;
    private String branch;
    private String birth;
    private String career;
    private String ntrp;
    private String refundAccount;
    private Bank refundBank;
    private String receiptInfo;
    private String trainerId;
    private boolean banned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LogginDto(UserEntity user) {
        this.identifier = user.getIdentifier();
        this.name = user.getName();
        this.role = user.getRole();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.branch = user.getBranch();
        this.birth = user.getBirth();
        this.career = user.getCareer();
        this.ntrp = user.getNtrp();
        this.refundAccount = user.getRefundAccount();
        this.refundBank = user.getRefundBank();
        this.receiptInfo = user.getReceiptInfo();
        this.trainerId = user.getTrainerId();
        this.banned = user.isBanned();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}