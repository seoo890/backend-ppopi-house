package com.ppopi.ppopihouse.diary.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diary_check_code")
@Getter
@Setter
@NoArgsConstructor
public class DiaryCheckCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_id")
    private Long checkId;

    @Column(name = "check_name", nullable = false)
    private String checkName;
}