package com.ppopi.ppopihouse.diary.domain;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import com.ppopi.ppopihouse.pet.domain.Pet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "diary_entry")
@Getter
@Setter
@NoArgsConstructor
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long diaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    private String memo;

    public void updateDiagnosis(Diagnosis diagnosis) {
        this.diagnosis = diagnosis;
    }
}