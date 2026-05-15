package com.ppopi.ppopihouse.hospital.external.google;

import java.util.List;

public record GooglePlaceResponse(
        List<GooglePlace> places
) {

    public record GooglePlace(
            String id,
            DisplayName displayName,
            String formattedAddress,
            String nationalPhoneNumber,
            String internationalPhoneNumber,
            Location location,
            RegularOpeningHours regularOpeningHours
    ) {
    }

    public record DisplayName(
            String text
    ) {
    }

    public record Location(
            Double latitude,
            Double longitude
    ) {
    }

    public record RegularOpeningHours(
            Boolean openNow,
            List<String> weekdayDescriptions,
            List<Period> periods
    ) {
    }

    public record Period(
            TimePoint open,
            TimePoint close
    ) {
    }

    public record TimePoint(
            Integer day,
            Integer hour,
            Integer minute
    ) {
    }
}