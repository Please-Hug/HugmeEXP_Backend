package org.example.hugmeexp.domain.studyRoom.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * StudyRoom 도메인에서 사용되는 Enum들을 정의한 클래스입니다.
 */
public class StudyRoomEnums {

    /**
     * 스터디홀 운영 상태
     */
    @Getter
    @RequiredArgsConstructor
    public enum OperatingStatus {
        OPEN("운영중", true),
        CLOSED("운영종료", false),
        MAINTENANCE("점검중", false),
        TEMPORARILY_CLOSED("임시휴업", false),
        COMING_SOON("오픈예정", false);

        private final String description;
        private final boolean available;

        public boolean isOperating() {
            return this == OPEN;
        }
    }

    /**
     * 예약 상태
     */
    @Getter
    @RequiredArgsConstructor
    public enum ReservationStatus {
        PENDING("대기중"),
        CONFIRMED("확정됨"),
        IN_PROGRESS("이용중"),
        COMPLETED("완료됨"),
        CANCELLED("취소됨"),
        NO_SHOW("노쇼"),
        EXPIRED("만료됨");

        private final String description;

        public boolean isActive() {
            return this == CONFIRMED || this == IN_PROGRESS;
        }

        public boolean isCancellable() {
            return this == PENDING || this == CONFIRMED;
        }
    }

    /**
     * 검색 정렬 기준
     */
    @Getter
    @RequiredArgsConstructor
    public enum SearchSortType {
        DISTANCE_ASC("거리순", "distance", "asc"),
        DISTANCE_DESC("거리순(멀리)", "distance", "desc"),
        NAME_ASC("이름순", "name", "asc"),
        NAME_DESC("이름순(역순)", "name", "desc"),
        RATING_DESC("평점순", "rating", "desc"),
        POPULARITY_DESC("인기순", "reservationCount", "desc"),
        PRICE_ASC("가격순", "price", "asc"),
        RECENTLY_ADDED("최신등록순", "createdAt", "desc");

        private final String description;
        private final String field;
        private final String direction;
    }

    /**
     * 검색 필터 타입
     */
    @Getter
    @RequiredArgsConstructor
    public enum SearchFilterType {
        ALL("전체"),
        AVAILABLE_NOW("지금 이용가능"),
        AVAILABLE_TODAY("오늘 이용가능"),
        HAS_PARKING("주차가능"),
        STUDY_ONLY("스터디전용"),
        GROUP_STUDY("그룹스터디"),
        INDIVIDUAL_STUDY("개인스터디");

        private final String description;
    }

    /**
     * 스터디룸 타입
     */
    @Getter
    @RequiredArgsConstructor
    public enum StudyRoomType {
        INDIVIDUAL("개인석", 1, 1),
        SMALL_GROUP("소그룹", 2, 4),
        MEDIUM_GROUP("중그룹", 5, 8),
        LARGE_GROUP("대그룹", 9, 12),
        CONFERENCE("회의실", 10, 20);

        private final String description;
        private final int minCapacity;
        private final int maxCapacity;

        public static StudyRoomType fromCapacity(int capacity) {
            for (StudyRoomType type : values()) {
                if (capacity >= type.minCapacity && capacity <= type.maxCapacity) {
                    return type;
                }
            }
            return INDIVIDUAL; // 기본값
        }
    }

    /**
     * 검색 정확도 레벨
     */
    @Getter
    @RequiredArgsConstructor
    public enum SearchAccuracyLevel {
        EXACT("정확일치", 1.0),
        HIGH("높음", 0.8),
        MEDIUM("보통", 0.6),
        LOW("낮음", 0.4);

        private final String description;
        private final double threshold;
    }
}