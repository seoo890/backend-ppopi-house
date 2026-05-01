package com.ppopi.ppopihouse.diagnosis.domain;

import lombok.Getter;

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
    private final String name;

    EyeSymptom(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EyeSymptom fromId(Long id) {
        for (EyeSymptom symptom : values()) {
            if (symptom.id.equals(id)) {
                return symptom;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 증상입니다.");
    }

}
