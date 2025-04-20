package com.example.YONDU.entity;

public enum Bank {
    KOREA_BANK("한국은행"),
    NH_BANK("농협은행"),
    SUHYUP_BANK("수협은행"),
    KDB_BANK("산업은행"),
    IBK_BANK("기업은행"),
    KEB_HANA_BANK("KEB하나은행"),
    KB_KOOKMIN_BANK("KB국민은행"),
    SHINHAN_BANK("신한은행"),
    WOORI_BANK("우리은행"),
    SC_FIRST_BANK("SC제일은행"),
    DGB_DAEGU_BANK("DGB대구은행"),
    BNK_BUSAN_BANK("BNK부산은행"),
    BNK_KYONGNAM_BANK("BNK경남은행"),
    KJB_JEONBUK_BANK("전북은행"),
    JJB_JEJU_BANK("제주은행"),
    KAKAO_BANK("카카오뱅크"),
    K_BANK("케이뱅크"),
    TOSS_BANK("토스뱅크"),
    POST_OFFICE_BANK("우체국예금보험"),
    CITI_BANK("한국씨티은행");

    private final String bankName;

    Bank(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }

    public static Bank fromString(String bankName) {
        for (Bank bank : Bank.values()) {
            if (bank.getBankName().equals(bankName)) {
                return bank;
            }
        }
        throw new IllegalArgumentException("Invalid bank name: " + bankName);
    }
}