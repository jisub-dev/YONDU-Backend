package com.example.YONDU.dto;

public class TrainerInfoDto {
    private String identifier;
    private String name;
    private String branch;

    public TrainerInfoDto(String identifier, String name, String branch) {
        this.identifier = identifier;
        this.name = name;
        this.branch = branch;
    }

    // Getter만 필요 (Lombok 써도 됨)
    public String getIdentifier() { return identifier; }
    public String getName() { return name; }
    public String getBranch() { return branch; }
}
