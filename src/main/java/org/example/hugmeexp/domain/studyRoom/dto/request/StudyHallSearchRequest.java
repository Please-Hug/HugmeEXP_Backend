package org.example.hugmeexp.domain.studyRoom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomConstants;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomEnums;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스터디홀 검색 요청")
public class StudyHallSearchRequest {

    @Schema(description = "검색 키워드 (이름, 주소 등)", example = "강남역")
    @Size(max = 100, message = "검색 키워드는 100자 이하여야 합니다.")
    private String keyword;

    @Schema(description = "현재 위치 위도", example = "37.5665")
    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
    private Double latitude;

    @Schema(description = "현재 위치 경도", example = "126.9780")
    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
    private Double longitude;

    @Schema(description = "검색 반경 (km)", example = "5.0", defaultValue = "10.0")
    @DecimalMin(value = "0.1", message = "검색 반경은 0.1km 이상이어야 합니다.")
    @DecimalMax(value = "50.0", message = "검색 반경은 50km 이하여야 합니다.")
    @Builder.Default
    private Double radius = StudyRoomConstants.DEFAULT_SEARCH_RADIUS_KM;

    @Schema(description = "결과 개수 제한", example = "20", defaultValue = "50")
    @Min(value = 1, message = "최소 1개 이상 조회해야 합니다.")
    @Max(value = 100, message = "최대 100개까지 조회 가능합니다.")
    @Builder.Default
    private Integer limit = StudyRoomConstants.DEFAULT_SEARCH_LIMIT;

    @Schema(description = "정렬 기준", example = "DISTANCE_ASC")
    @Builder.Default
    private StudyRoomEnums.SearchSortType sortType = StudyRoomEnums.SearchSortType.DISTANCE_ASC;

    @Schema(description = "검색 필터 목록")
    @Builder.Default
    private Set<StudyRoomEnums.SearchFilterType> filters = Set.of();

    @Schema(description = "최소 수용 인원", example = "1")
    @Min(value = 1, message = "최소 인원은 1명 이상이어야 합니다.")
    private Integer minCapacity;

    @Schema(description = "최대 수용 인원", example = "10")
    @Max(value = 20, message = "최대 인원은 20명 이하여야 합니다.")
    private Integer maxCapacity;

    @Schema(description = "검색 시간 (ISO 형식)", example = "2024-12-26T14:00:00")
    private String searchDateTime;

    @Schema(description = "타임존 ID", example = "Asia/Seoul", defaultValue = "Asia/Seoul")
    @Builder.Default
    private String timeZoneId = "Asia/Seoul";

    // === Validation Methods ===

    /**
     * 위치 정보 유효성 검증
     */
    public boolean hasValidLocationInfo() {
        return latitude != null && longitude != null &&
                latitude >= StudyRoomConstants.KOREA_MIN_LATITUDE &&
                latitude <= StudyRoomConstants.KOREA_MAX_LATITUDE &&
                longitude >= StudyRoomConstants.KOREA_MIN_LONGITUDE &&
                longitude <= StudyRoomConstants.KOREA_MAX_LONGITUDE;
    }

    /**
     * 검색 키워드 유효성 검증
     */
    public boolean hasValidKeyword() {
        return StringUtils.hasText(keyword) && keyword.trim().length() >= 2;
    }

    /**
     * 인원 범위 검증
     */
    public boolean hasValidCapacityRange() {
        if (minCapacity == null && maxCapacity == null) {
            return true;
        }
        if (minCapacity != null && maxCapacity != null) {
            return minCapacity <= maxCapacity;
        }
        return true;
    }

    /**
     * 검색 시간 파싱 및 검증
     */
    public LocalDateTime getParsedSearchDateTime() {
        if (!StringUtils.hasText(searchDateTime)) {
            return LocalDateTime.now(ZoneId.of(timeZoneId));
        }

        try {
            return LocalDateTime.parse(searchDateTime);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("잘못된 검색 시간 형식입니다: " + searchDateTime, e);
        }
    }

    /**
     * 타임존 검증
     */
    public ZoneId getValidatedTimeZone() {
        try {
            return ZoneId.of(timeZoneId);
        } catch (Exception e) {
            throw new IllegalArgumentException("지원하지 않는 타임존입니다: " + timeZoneId, e);
        }
    }

    // === Utility Methods ===

    /**
     * 위치 기반 검색 여부 확인
     */
    public boolean isLocationBasedSearch() {
        return hasValidLocationInfo();
    }

    /**
     * 키워드 기반 검색 여부 확인
     */
    public boolean isKeywordBasedSearch() {
        return hasValidKeyword();
    }

    /**
     * 필터 적용 여부 확인
     */
    public boolean hasFilters() {
        return filters != null && !filters.isEmpty();
    }

    /**
     * 캐시 키 생성용 toString
     */
    @Override
    public String toString() {
        return String.format("search_%s_%s_%s_%s_%s_%s_%s",
                Objects.toString(keyword, ""),
                Objects.toString(latitude, ""),
                Objects.toString(longitude, ""),
                Objects.toString(radius, ""),
                Objects.toString(limit, ""),
                Objects.toString(sortType, ""),
                Objects.toString(filters.hashCode(), ""));
    }

    // === Builder 확장 ===
    public static class StudyHallSearchRequestBuilder {

        public StudyHallSearchRequestBuilder nearbySearch(Double lat, Double lng, Double radiusKm) {
            return this.latitude(lat)
                    .longitude(lng)
                    .radius(radiusKm)
                    .sortType(StudyRoomEnums.SearchSortType.DISTANCE_ASC);
        }

        public StudyHallSearchRequestBuilder keywordSearch(String searchKeyword) {
            return this.keyword(searchKeyword)
                    .sortType(StudyRoomEnums.SearchSortType.NAME_ASC);
        }

        public StudyHallSearchRequestBuilder withCapacityRange(Integer min, Integer max) {
            return this.minCapacity(min)
                    .maxCapacity(max);
        }

        public StudyHallSearchRequestBuilder availableNow() {
            return this.filters(Set.of(StudyRoomEnums.SearchFilterType.AVAILABLE_NOW));
        }
    }
}