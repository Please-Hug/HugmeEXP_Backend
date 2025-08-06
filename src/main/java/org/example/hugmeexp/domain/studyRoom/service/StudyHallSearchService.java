package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomConstants;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomEnums;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.projection.StudyHallWithDistanceProjection;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 스터디홀 검색 전용 서비스 - 검색 로직을 분리하여 성능과 유지보수성 향상
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyHallSearchService {

    private final StudyHallRepository studyHallRepository;

    /**
     * 통합 검색 - 키워드, 위치, 필터 조건을 모두 지원
     */
    @Cacheable(value = StudyRoomConstants.NEARBY_HALLS_CACHE,
            key = "#request.toString() + '_' + #sortType.name()",
            unless = "#result.isEmpty()")
    public List<StudyHallLocationResponse> searchStudyHalls(
            StudyHallSearchRequest request,
            StudyRoomEnums.SearchSortType sortType,
            Set<StudyRoomEnums.SearchFilterType> filters) {

        log.info("통합 검색 시작 - keyword: {}, lat: {}, lng: {}, filters: {}",
                request.getKeyword(), request.getLatitude(), request.getLongitude(), filters);

        List<StudyHallLocationResponse> results = new ArrayList<>();

        // 1. 위치 기반 검색이 있는 경우
        if (hasLocationInfo(request)) {
            results = searchByLocation(request);
        }
        // 2. 키워드만 있는 경우
        else if (StringUtils.hasText(request.getKeyword())) {
            results = searchByKeyword(request.getKeyword(), request.getLimit());
        }
        // 3. 전체 조회
        else {
            results = getAllStudyHalls(request.getLimit());
        }

        // 필터 적용
        if (!filters.isEmpty()) {
            results = applyFilters(results, filters);
        }

        // 정렬 적용
        results = applySorting(results, sortType, request.getLatitude(), request.getLongitude());

        log.info("검색 완료 - {} 개 결과 반환", results.size());
        return results.stream().limit(request.getLimit()).collect(Collectors.toList());
    }

    /**
     * 스마트 키워드 검색 - 유사도 기반 매칭
     */
    @Cacheable(value = "smartSearch", key = "#keyword + '_' + #limit")
    public List<StudyHallLocationResponse> smartKeywordSearch(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }

        String normalizedKeyword = normalizeKeyword(keyword);
        List<StudyHall> allHalls = studyHallRepository.findAllWithStudyRooms();

        // 유사도 점수 계산 후 정렬
        return allHalls.stream()
                .map(hall -> {
                    double score = calculateSimilarityScore(hall, normalizedKeyword);
                    return new ScoredStudyHall(hall, score);
                })
                .filter(scored -> scored.score >= StudyRoomEnums.SearchAccuracyLevel.LOW.getThreshold())
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(Objects.requireNonNullElse(limit, StudyRoomConstants.DEFAULT_SEARCH_LIMIT))
                .map(scored -> StudyHallLocationResponse.from(scored.hall))
                .collect(Collectors.toList());
    }

    /**
     * 자동완성 검색
     */
    @Cacheable(value = "autocomplete", key = "#prefix")
    public List<String> getAutocompleteSuggestions(String prefix, int limit) {
        if (!StringUtils.hasText(prefix) || prefix.length() < 2) {
            return Collections.emptyList();
        }

        List<StudyHall> halls = studyHallRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(prefix);

        Set<String> suggestions = new LinkedHashSet<>();

        for (StudyHall hall : halls) {
            // 이름 기반 제안
            if (StringUtils.hasText(hall.getName()) &&
                    hall.getName().toLowerCase().contains(prefix.toLowerCase())) {
                suggestions.add(hall.getName());
            }

            // 주소 기반 제안
            if (StringUtils.hasText(hall.getSimpleAddress()) &&
                    hall.getSimpleAddress().toLowerCase().contains(prefix.toLowerCase())) {
                suggestions.add(hall.getSimpleAddress());
            }

            if (suggestions.size() >= limit) break;
        }

        return new ArrayList<>(suggestions);
    }

    /**
     * 고급 검색 - 복합 조건
     */
    public List<StudyHallLocationResponse> advancedSearch(AdvancedSearchCriteria criteria) {
        log.info("고급 검색 시작: {}", criteria);

        // Specification 패턴 적용으로 동적 쿼리 구성
        List<StudyHall> halls = studyHallRepository.findAllWithStudyRooms();

        return halls.stream()
                .filter(hall -> matchesAdvancedCriteria(hall, criteria))
                .map(StudyHallLocationResponse::from)
                .collect(Collectors.toList());
    }

    // === Private Helper Methods ===

    private boolean hasLocationInfo(StudyHallSearchRequest request) {
        return request.getLatitude() != null && request.getLongitude() != null;
    }

    private List<StudyHallLocationResponse> searchByLocation(StudyHallSearchRequest request) {
        BoundingBox boundingBox = calculateBoundingBox(request);

        List<StudyHallWithDistanceProjection> projections = studyHallRepository
                .findNearbyStudyHallsWithProjection(
                        request.getLatitude(), request.getLongitude(),
                        boundingBox.getMinLat(), boundingBox.getMaxLat(),
                        boundingBox.getMinLng(), boundingBox.getMaxLng(),
                        request.getRadius(), request.getLimit()
                );

        return projections.stream()
                .map(this::convertProjectionToResponse)
                .collect(Collectors.toList());
    }

    private List<StudyHallLocationResponse> searchByKeyword(String keyword, Integer limit) {
        // 1차: 이름으로 검색
        List<StudyHall> nameResults = studyHallRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(keyword);

        // 2차: 주소로 검색
        List<StudyHall> addressResults = studyHallRepository
                .findByLocationAddressContainingIgnoreCase(keyword);

        // 중복 제거 후 합치기
        Set<StudyHall> combinedResults = new LinkedHashSet<>(nameResults);
        combinedResults.addAll(addressResults);

        return combinedResults.stream()
                .limit(Objects.requireNonNullElse(limit, StudyRoomConstants.DEFAULT_SEARCH_LIMIT))
                .map(StudyHallLocationResponse::from)
                .collect(Collectors.toList());
    }

    private List<StudyHallLocationResponse> getAllStudyHalls(Integer limit) {
        Pageable pageable = PageRequest.of(0,
                Objects.requireNonNullElse(limit, StudyRoomConstants.DEFAULT_SEARCH_LIMIT));

        Page<StudyHall> page = studyHallRepository.findAllByIsDeletedFalse(pageable);

        return page.getContent().stream()
                .map(StudyHallLocationResponse::from)
                .collect(Collectors.toList());
    }

    private List<StudyHallLocationResponse> applyFilters(
            List<StudyHallLocationResponse> results,
            Set<StudyRoomEnums.SearchFilterType> filters) {

        return results.stream()
                .filter(hall -> matchesFilters(hall, filters))
                .collect(Collectors.toList());
    }

    private boolean matchesFilters(StudyHallLocationResponse hall, Set<StudyRoomEnums.SearchFilterType> filters) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        for (StudyRoomEnums.SearchFilterType filter : filters) {
            switch (filter) {
                case AVAILABLE_NOW -> {
                    if (!isCurrentlyOpen(hall, now)) return false;
                }
                case AVAILABLE_TODAY -> {
                    if (!isOpenToday(hall, now.toLocalDate())) return false;
                }
                // TODO: 추가 필터 로직 구현
                default -> {
                    // 기본적으로 통과
                }
            }
        }
        return true;
    }

    private List<StudyHallLocationResponse> applySorting(
            List<StudyHallLocationResponse> results,
            StudyRoomEnums.SearchSortType sortType,
            Double latitude, Double longitude) {

        Comparator<StudyHallLocationResponse> comparator = switch (sortType) {
            case DISTANCE_ASC -> Comparator.comparing(
                    hall -> Optional.ofNullable(hall.getDistance()).orElse(Double.MAX_VALUE));
            case DISTANCE_DESC -> Comparator.comparing(
                    (StudyHallLocationResponse hall) -> Optional.ofNullable(hall.getDistance()).orElse(0.0)).reversed();
            case NAME_ASC -> Comparator.comparing(StudyHallLocationResponse::getName);
            case NAME_DESC -> Comparator.comparing(StudyHallLocationResponse::getName).reversed();
            case RECENTLY_ADDED -> Comparator.comparing(StudyHallLocationResponse::getId).reversed();
            default -> Comparator.comparing(StudyHallLocationResponse::getId);
        };

        return results.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    // 키워드 정규화
    private String normalizeKeyword(String keyword) {
        return keyword.toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();
    }

    // 유사도 점수 계산
    private double calculateSimilarityScore(StudyHall hall, String keyword) {
        double score = 0.0;

        // 이름 매칭 (가중치 0.6)
        if (StringUtils.hasText(hall.getName())) {
            String normalizedName = normalizeKeyword(hall.getName());
            if (normalizedName.contains(keyword)) {
                score += 0.6 * (keyword.length() / (double) normalizedName.length());
            }
        }

        // 주소 매칭 (가중치 0.3)
        if (StringUtils.hasText(hall.getSimpleAddress())) {
            String normalizedAddress = normalizeKeyword(hall.getSimpleAddress());
            if (normalizedAddress.contains(keyword)) {
                score += 0.3 * (keyword.length() / (double) normalizedAddress.length());
            }
        }

        // 설명 매칭 (가중치 0.1)
        if (StringUtils.hasText(hall.getDescription())) {
            String normalizedDescription = normalizeKeyword(hall.getDescription());
            if (normalizedDescription.contains(keyword)) {
                score += 0.1;
            }
        }

        return score;
    }

    // 기타 Helper Methods
    private BoundingBox calculateBoundingBox(StudyHallSearchRequest request) {
        double kmPerDegreeLat = StudyRoomConstants.KM_PER_DEGREE_LAT;
        double kmPerDegreeLng = StudyRoomConstants.KM_PER_DEGREE_LAT *
                Math.cos(Math.toRadians(request.getLatitude()));

        double deltaLat = request.getRadius() / kmPerDegreeLat;
        double deltaLng = request.getRadius() / kmPerDegreeLng;

        return new BoundingBox(
                request.getLatitude() - deltaLat,
                request.getLatitude() + deltaLat,
                request.getLongitude() - deltaLng,
                request.getLongitude() + deltaLng
        );
    }

    private StudyHallLocationResponse convertProjectionToResponse(StudyHallWithDistanceProjection projection) {
        return StudyHallLocationResponse.builder()
                .id(projection.getId())
                .name(projection.getName())
                .description(projection.getDescription())
                .simpleAddress(projection.getSimpleAddress())
                .address(projection.getAddress())
                .latitude(projection.getLatitude())
                .longitude(projection.getLongitude())
                .thumbnail(projection.getThumbnail())
                .openTime(projection.getOpenTime())
                .closeTime(projection.getCloseTime())
                .distance(projection.getDistance())
                .build();
    }

    private boolean isCurrentlyOpen(StudyHallLocationResponse hall, LocalDateTime now) {
        if (hall.getOpenTime() == null || hall.getCloseTime() == null) {
            return true; // 24시간 운영으로 가정
        }

        var currentTime = now.toLocalTime();
        return !currentTime.isBefore(hall.getOpenTime()) &&
                !currentTime.isAfter(hall.getCloseTime());
    }

    private boolean isOpenToday(StudyHallLocationResponse hall, java.time.LocalDate date) {
        // TODO: 휴무일 체크 로직 구현
        return true;
    }

    private boolean matchesAdvancedCriteria(StudyHall hall, AdvancedSearchCriteria criteria) {
        // TODO: 고급 검색 조건 매칭 로직 구현
        return true;
    }

    // Inner Classes
    record BoundingBox(double minLat, double maxLat, double minLng, double maxLng) {}

    record ScoredStudyHall(StudyHall hall, double score) {}

    public record AdvancedSearchCriteria(
            String keyword,
            Double latitude, Double longitude, Double radius,
            Integer minCapacity, Integer maxCapacity,
            Set<StudyRoomEnums.SearchFilterType> filters,
            StudyRoomEnums.SearchSortType sortType
    ) {}
}