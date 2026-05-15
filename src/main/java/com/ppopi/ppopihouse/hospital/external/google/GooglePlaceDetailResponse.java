package com.ppopi.ppopihouse.hospital.external.google;

public record GooglePlaceDetailResponse(
        String id,
        GooglePlaceResponse.DisplayName displayName,
        String formattedAddress,
        String nationalPhoneNumber,
        String internationalPhoneNumber,
        GooglePlaceResponse.Location location,
        GooglePlaceResponse.RegularOpeningHours regularOpeningHours
) {
}