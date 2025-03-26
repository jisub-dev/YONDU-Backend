package com.example.YONDU.entity;

public enum ReceiptBoolean {
    발급("ISSUE"),
    미발급("NOT_ISSUED");

    private final String label;

    ReceiptBoolean(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
