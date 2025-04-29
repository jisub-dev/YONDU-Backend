package com.example.YONDU.dto.notice;

import com.example.YONDU.entity.notice.Notice;

import java.time.LocalDateTime;

public record NoticeDto(
        Long noticeId,
        String title,
        String content,
        LocalDateTime createdAt,
        String author
) {
    public static NoticeDto from(Notice notice) {
        return new NoticeDto(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getAuthor()
        );
    }
}
