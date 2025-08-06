package org.example.hugmeexp.domain.studyRoom.util;

import org.example.hugmeexp.domain.studyRoom.entity.Location;

/**
 * 거리 계산 전용 유틸리티 클래스
 * Location과 KakaoMapService에서 공통으로 사용
 */
public class DistanceCalculator {

    private static final double EARTH_RADIUS = 6371; // 지구 반지름 (km)

    /**
     * Haversine formula를 사용한 두 지점 간 거리 계산
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 거리 (km)
     */
    public static Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return null;
        }

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return Math.round(distance * 100.0) / 100.0; // 소수점 둘째 자리까지
    }
}