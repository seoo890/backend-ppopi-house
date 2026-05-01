package com.ppopi.ppopihouse.hospital.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hospital")
@Getter
@Setter
@NoArgsConstructor
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long hospitalId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "call_number", nullable = false)
    private String callNumber;

    @Column(name = "business_hours", nullable = false)
    private String businessHours;

    @Column(name = "is_24hr", nullable = false)
    private boolean is24hr;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;
}