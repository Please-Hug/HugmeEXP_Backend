package org.example.hugmeexp.domain.studyRoom.constants;

import java.time.LocalTime;

/**
 * StudyRoom 도메인에서 사용되는 상수들을 정의한 클래스입니다.
 */
public final class StudyRoomConstants {

    // === 시간 관련 상수 ===
    public static final LocalTime DEFAULT_OPEN_TIME = LocalTime.of(9, 0);
    public static final LocalTime DEFAULT_CLOSE_TIME = LocalTime.of(22, 0);
    public static final int DEFAULT_SLOT_DURATION_HOURS = 1;
    public static final int MIN_RESERVATION_DURATION_HOURS = 1;
    public static final int MAX_RESERVATION_DURATION_HOURS = 8;

    // === 검색 관련 상수 ===
    public static final double DEFAULT_SEARCH_RADIUS_KM = 10.0;
    public static final double MAX_SEARCH_RADIUS_KM = 50.0;
    public static final double MIN_SEARCH_RADIUS_KM = 0.1;
    public static final int DEFAULT_SEARCH_LIMIT = 50;
    public static final int MAX_SEARCH_LIMIT = 100;

    // === 좌표 관련 상수 ===
    public static final double KOREA_MIN_LATITUDE = 32.0;
    public static final double KOREA_MAX_LATITUDE = 39.0;
    public static final double KOREA_MIN_LONGITUDE = 123.0;
    public static final double KOREA_MAX_LONGITUDE = 132.0;

    // === 거리 계산 상수 ===
    public static final double EARTH_RADIUS_KM = 6371.0;
    public static final double KM_PER_DEGREE_LAT = 111.0;

    // === 캐시 관련 상수 ===
    public static final String STUDY_HALLS_CACHE = "studyHalls";
    public static final String STUDY_ROOMS_CACHE = "studyRooms";
    public static final String NEARBY_HALLS_CACHE = "nearbyHalls";

    // === 용량 관련 상수 ===
    public static final int MIN_ROOM_CAPACITY = 1;
    public static final int MAX_ROOM_CAPACITY = 20;
    public static final int DEFAULT_ROOM_CAPACITY = 4;

    // === 메시지 상수 ===
    public static final String SUCCESS_MESSAGE_FORMAT = "%s이(가) 성공적으로 %s되었습니다.";
    public static final String SEARCH_RESULT_MESSAGE_FORMAT = "%s으로 %d개의 결과를 찾았습니다.";

    private StudyRoomConstants() {
        throw new UnsupportedOperationException("상수 클래스는 인스턴스화할 수 없습니다.");
    }
}