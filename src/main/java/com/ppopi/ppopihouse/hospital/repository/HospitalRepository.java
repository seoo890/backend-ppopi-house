package com.ppopi.ppopihouse.hospital.repository;

import com.ppopi.ppopihouse.hospital.domain.Hospital;
import com.ppopi.ppopihouse.hospital.dto.projection.HospitalDistanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    @Query(value = """
            SELECT
                h.hospital_id AS hospitalId,
                h.name AS name,
                h.address AS address,
                h.call_number AS callNumber,
                h.business_hours AS businessHours,
                h.is_24hr AS is24hr,
                h.latitude AS latitude,
                h.longitude AS longitude,
                CAST(ROUND(
                    6371000 * ACOS(
                        LEAST(1, GREATEST(-1,
                            COS(RADIANS(:centerLat)) *
                            COS(RADIANS(h.latitude)) *
                            COS(RADIANS(h.longitude) - RADIANS(:centerLng)) +
                            SIN(RADIANS(:centerLat)) *
                            SIN(RADIANS(h.latitude))
                        ))
                    )
                ) AS BIGINT) AS distanceMeter
            FROM hospital h
            WHERE h.latitude BETWEEN :southWestLat AND :northEastLat
              AND h.longitude BETWEEN :southWestLng AND :northEastLng
              AND (:emergencyOnly = false OR h.is_24hr = true)
            ORDER BY distanceMeter ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<HospitalDistanceProjection> findHospitalsInBoundsOrderByDistance(
            @Param("southWestLat") double southWestLat,
            @Param("northEastLat") double northEastLat,
            @Param("southWestLng") double southWestLng,
            @Param("northEastLng") double northEastLng,
            @Param("centerLat") double centerLat,
            @Param("centerLng") double centerLng,
            @Param("emergencyOnly") boolean emergencyOnly,
            @Param("limit") int limit
    );

}