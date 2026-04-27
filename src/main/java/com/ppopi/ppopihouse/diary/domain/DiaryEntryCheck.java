package com.ppopi.ppopihouse.diary.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diary_entry_check")
@Getter
@Setter
@NoArgsConstructor
public class DiaryEntryCheck {

    @EmbeddedId
    private DiaryEntryCheckId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")
    @JoinColumn(name = "diary_id")
    private DiaryEntry diaryEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("checkId")
    @JoinColumn(name = "check_id")
    private DiaryCheckCode checkCode;
}