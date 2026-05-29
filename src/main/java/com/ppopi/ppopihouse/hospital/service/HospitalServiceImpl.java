package com.ppopi.ppopihouse.hospital.service;

import com.ppopi.ppopihouse.hospital.dto.request.HospitalSearchRequest;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalDetailResponse;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalListResponse;
import com.ppopi.ppopihouse.hospital.external.google.GooglePlacesClient;
import com.ppopi.ppopihouse.hospital.external.kakao.KakaoLocalClient;
import com.ppopi.ppopihouse.hospital.external.kakao.KakaoPlaceResponse;
import com.ppopi.ppopihouse.hospital.cache.HospitalCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import com.ppopi.ppopihouse.hospital.external.google.GooglePlaceResponse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private static final int DEFAULT_LIMIT = 15;
    private final GooglePlacesClient googlePlacesClient;
    private final KakaoLocalClient kakaoLocalClient;
    private final HospitalCacheService hospitalCacheService;

    @Override
    public List<HospitalListResponse> getHospitals(HospitalSearchRequest request) {
        validateSearchRequest(request);

        double centerLat = request.getCenter().getLat();
        double centerLng = request.getCenter().getLng();

        return kakaoLocalClient
                .searchAnimalHospitals(centerLat, centerLng, DEFAULT_LIMIT)
                .stream()
                .filter(kakaoPlace -> isInBounds(kakaoPlace, request))
                .map(kakaoPlace -> {
                    hospitalCacheService.saveCoordinate(kakaoPlace);
                    
                    double hospitalLat = Double.parseDouble(kakaoPlace.y());
                    double hospitalLng = Double.parseDouble(kakaoPlace.x());

                    long distanceMeter = calculateDistanceMeter(
                            centerLat,
                            centerLng,
                            hospitalLat,
                            hospitalLng
                    );

                    GooglePlaceResponse.GooglePlace googlePlace =
                            googlePlacesClient.searchPlaceForOpeningHours(
                                    kakaoPlace.place_name(),
                                    hospitalLat,
                                    hospitalLng
                            );

                    boolean is24hr = googlePlace != null && is24Hours(googlePlace);

                    return HospitalListResponse.from(
                            kakaoPlace,
                            is24hr,
                            distanceMeter
                    );
                })
                .toList();
    }

    @Override
    public HospitalDetailResponse getHospital(String hospitalId, double centerLat, double centerLng) {
        double[] hospitalCoordinate = hospitalCacheService.getCoordinate(hospitalId);

        if (hospitalCoordinate == null) {
            throw new NoSuchElementException("병원 정보가 만료되었습니다. 병원 목록을 다시 조회해 주세요.");
        }

        double hospitalLat = hospitalCoordinate[0];
        double hospitalLng = hospitalCoordinate[1];

        KakaoPlaceResponse.Document kakaoPlace = kakaoLocalClient
            .searchAnimalHospitals(hospitalLat, hospitalLng, DEFAULT_LIMIT)
            .stream()
            .filter(place -> hospitalId.equals(place.id()))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 병원입니다."));

        long distanceMeter = calculateDistanceMeter(
                centerLat,
                centerLng,
                hospitalLat,
                hospitalLng
        );

        GooglePlaceResponse.GooglePlace googlePlace =
                googlePlacesClient.searchPlaceForOpeningHours(
                        kakaoPlace.place_name(),
                        hospitalLat,
                        hospitalLng
                );

        String businessHours = googlePlace != null
                ? getBusinessHours(googlePlace)
                : "10:00 - 20:00";

        boolean is24hr = googlePlace != null && is24Hours(googlePlace);

        String operationLabel = googlePlace != null
                ? getOperationLabel(googlePlace)
                : "영업시간 확인 필요";

        return HospitalDetailResponse.from(
                kakaoPlace,
                googlePlace,
                businessHours,
                operationLabel,
                is24hr,
                distanceMeter
        );
    }

    private long calculateDistanceMeter(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(earthRadius * c);
    }

    private String getBusinessHours(GooglePlaceResponse.GooglePlace place) {
        if (place.regularOpeningHours() == null) {
            return "10:00 - 20:00";
        }

        // 1순위: weekdayDescriptions가 있으면 오늘 요일 설명에서 추출
        if (place.regularOpeningHours().weekdayDescriptions() != null
                && !place.regularOpeningHours().weekdayDescriptions().isEmpty()) {
            String todayPrefix = getTodayKoreanPrefix();

            return place.regularOpeningHours().weekdayDescriptions().stream()
                    .filter(description -> description.startsWith(todayPrefix))
                    .findFirst()
                    .map(this::formatTodayBusinessHours)
                    .orElse("10:00 - 20:00");
        }

        // 2순위: weekdayDescriptions가 없으면 periods로 오늘 영업시간 계산
        if (place.regularOpeningHours().periods() != null
                && !place.regularOpeningHours().periods().isEmpty()) {
            return getTodayBusinessHoursFromPeriods(place.regularOpeningHours().periods());
        }

        return "10:00 - 20:00";
    }

    private boolean is24Hours(GooglePlaceResponse.GooglePlace place) {
        if (place.regularOpeningHours() == null
                || place.regularOpeningHours().weekdayDescriptions() == null) {
            return false;
        }

        return place.regularOpeningHours().weekdayDescriptions().stream()
                .anyMatch(description ->
                        description.contains("Open 24 hours")
                                || description.contains("24시간")
                                || description.contains("24 hours")
                );
    }

    private String getOperationLabel(GooglePlaceResponse.GooglePlace place) {
        if (place.regularOpeningHours() == null
                || place.regularOpeningHours().openNow() == null) {
            return getOperationLabelByDefaultHours();
        }

        return Boolean.TRUE.equals(place.regularOpeningHours().openNow())
                ? "영업 중"
                : "영업 종료";
    }

    private String getOperationLabelByDefaultHours() {
        LocalTime now = LocalTime.now();

        LocalTime open = LocalTime.of(10, 0);
        LocalTime close = LocalTime.of(20, 0);

        return !now.isBefore(open) && now.isBefore(close)
                ? "영업 중"
                : "영업 종료";
    }

    private void validateSearchRequest(HospitalSearchRequest request) {
        if (request == null
                || request.getBounds() == null
                || request.getBounds().getNortheast() == null
                || request.getBounds().getSouthwest() == null
                || request.getCenter() == null
                || request.getCenter().getLat() == null
                || request.getCenter().getLng() == null) {
            throw new IllegalArgumentException("지도 검색 요청 값이 올바르지 않습니다.");
        }
    }

    private boolean isInBounds(
            KakaoPlaceResponse.Document place,
            HospitalSearchRequest request
    ) {
        double lat = Double.parseDouble(place.y());
        double lng = Double.parseDouble(place.x());

        double north = request.getBounds().getNortheast().getLat();
        double south = request.getBounds().getSouthwest().getLat();
        double east = request.getBounds().getNortheast().getLng();
        double west = request.getBounds().getSouthwest().getLng();

        return lat >= south
                && lat <= north
                && lng >= west
                && lng <= east;
    }

    private String getTodayKoreanPrefix() {
        return switch (LocalDate.now().getDayOfWeek()) {
            case MONDAY -> "월요일:";
            case TUESDAY -> "화요일:";
            case WEDNESDAY -> "수요일:";
            case THURSDAY -> "목요일:";
            case FRIDAY -> "금요일:";
            case SATURDAY -> "토요일:";
            case SUNDAY -> "일요일:";
        };
    }

    private String formatTodayBusinessHours(String todayDescription) {
        String hours = todayDescription
                .replaceFirst("^[가-힣]+요일:\\s*", "")
                .replace("\u202F", " ")
                .replace("\u2009", " ")
                .replace("\u00A0", " ")
                .trim();

        if (hours.contains("휴무")) {
            return "휴무";
        }

        if (hours.contains("24시간")
                || hours.contains("24 hours")
                || hours.contains("Open 24 hours")) {
            return "00:00 - 24:00";
        }

        String[] parts = hours.split("\\s*[~–-]\\s*");

        if (parts.length < 2) {
            return "10:00 - 20:00";
        }

        String openText = parts[0].trim();
        String closeText = parts[1].trim();

        if (!closeText.contains("오전") && !closeText.contains("오후")) {
            if (openText.contains("오후")) {
                closeText = "오후 " + closeText;
            } else if (openText.contains("오전")) {
                closeText = "오후 " + closeText;
            }
        }

        String open = convertKoreanAmPmTo24Hour(openText);
        String close = convertKoreanAmPmTo24Hour(closeText);

        return open + " - " + close;
    }

    private String convertKoreanAmPmTo24Hour(String timeText) {
        String text = timeText
                .replace("\u202F", " ")
                .replace("\u2009", " ")
                .replace("\u00A0", " ")
                .replaceAll("[^0-9:오전오후]", "")
                .trim();

        boolean isAm = text.contains("오전");
        boolean isPm = text.contains("오후");

        text = text
                .replace("오전", "")
                .replace("오후", "")
                .trim();

        String[] timeParts = text.split(":");

        int hour = Integer.parseInt(timeParts[0]);
        int minute = timeParts.length > 1
                ? Integer.parseInt(timeParts[1])
                : 0;

        if (isPm && hour < 12) {
            hour += 12;
        }

        if (isAm && hour == 12) {
            hour = 0;
        }

        return String.format("%02d:%02d", hour, minute);
    }

    private String getTodayBusinessHoursFromPeriods(
            List<GooglePlaceResponse.Period> periods
    ) {
        int today = convertToGoogleDay(LocalDate.now().getDayOfWeek());

        return periods.stream()
                .filter(period -> period.open() != null)
                .filter(period -> period.open().day() != null)
                .filter(period -> period.open().day() == today)
                .findFirst()
                .map(period -> {
                    if (period.open() == null || period.close() == null) {
                        return "00:00 - 24:00";
                    }

                    String open = formatTime(period.open().hour(), period.open().minute());
                    String close = formatTime(period.close().hour(), period.close().minute());

                    return open + " - " + close;
                })
                .orElse("10:00 - 20:00");
    }

    private int convertToGoogleDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case SUNDAY -> 0;
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
        };
    }

    private String formatTime(Integer hour, Integer minute) {
        int h = hour != null ? hour : 0;
        int m = minute != null ? minute : 0;

        if (h == 24 && m == 0) {
            return "24:00";
        }

        return String.format("%02d:%02d", h, m);
    }

    private String cleanHtml(String text) {
        if (text == null) {
            return "";
        }

        return text.replaceAll("<[^>]*>", "");
    }
}
