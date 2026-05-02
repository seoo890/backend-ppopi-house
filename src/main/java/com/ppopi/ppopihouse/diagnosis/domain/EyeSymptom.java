package com.ppopi.ppopihouse.diagnosis.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum EyeSymptom {

    REDNESS(1L, "충혈"),
    DISCHARGE(2L, "눈 비빔"),
    EYE_GUNK(3L, "분비물/눈곱"),
    TEARING(4L, "눈물"),
    SQUINTING(5L, "눈 찡그림"),
    CLOUDY(6L, "혼탁"),
    PAIN_SUSPECTED(7L, "통증 의심");

    private final Long id;
    private final String description;

    EyeSymptom(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    private static final Map<Long, EyeSymptom> MAP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(EyeSymptom::getId, s -> s));

    public static EyeSymptom fromId(Long id) {
        EyeSymptom symptom = MAP.get(id);
        if (symptom == null) {
            throw new IllegalArgumentException("존재하지 않는 증상입니다.");
        }
        return symptom;
    }

}
