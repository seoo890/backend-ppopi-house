package com.ppopi.ppopihouse.diary.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class DiaryEntryCheckId implements Serializable {

    private Long diaryId;
    private Long checkId;
}