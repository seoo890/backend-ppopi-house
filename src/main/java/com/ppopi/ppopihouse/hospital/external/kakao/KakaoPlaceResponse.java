package com.ppopi.ppopihouse.hospital.external.kakao;

import java.util.List;

public record KakaoPlaceResponse(
        List<Document> documents
) {

    public record Document(
            String id,
            String place_name,
            String address_name,
            String road_address_name,
            String phone,
            String x,
            String y
    ) {
    }
}