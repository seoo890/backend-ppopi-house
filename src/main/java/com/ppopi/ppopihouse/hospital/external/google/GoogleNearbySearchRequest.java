package com.ppopi.ppopihouse.hospital.external.google;

import java.util.List;

public record GoogleNearbySearchRequest(
        List<String> includedTypes,
        int maxResultCount,
        LocationRestriction locationRestriction
) {
    public record LocationRestriction(
            Circle circle
    ) {}

    public record Circle(
            Center center,
            double radius
    ) {}

    public record Center(
            double latitude,
            double longitude
    ) {}
}