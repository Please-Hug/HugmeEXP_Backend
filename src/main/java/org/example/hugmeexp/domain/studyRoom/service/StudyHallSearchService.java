package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.projection.StudyHallWithDistanceProjection;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.reids.service.RedisAutoCompleteService;
import org.example.hugmeexp.domain.studyRoom.reids.service.RedisGeoService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 스터디홀 검색 전용 서비스 - 검색 로직을 분리하여 성능과 유지보수성 향상
 */

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudyHallSearchService {

    private final StudyHallRepository studyHallRepository;
    private final RedisGeoService redisGeoService;
    private final RedisAutoCompleteService autoCompleteService;

    /**
     * Redis Geo를 활용한 위치 기반 검색 (성능 측정 제거)
     */
    @Cacheable(value = "nearbyHallsRedis", key = "#request.toString()", unless = "#result.isEmpty()")
    public List<StudyHallLocationResponse> searchNearbyStudyHallsWithRedis(StudyHallSearchRequest request) {
        try {
            List<StudyHallLocationResponse> redisResults = redisGeoService.findNearbyStudyHalls(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadius(),
                    request.getLimit()
            );

            if (!redisResults.isEmpty()) {
                return applyAdditionalFilters(redisResults, request);
            }

            // Redis에 데이터가 없으면 DB 검색 (Fallback)
            return searchNearbyStudyHallsWithDB(request);

        } catch (Exception e) {
            log.debug("Redis Geo 검색 실패, DB 검색으로 Fallback");
            return searchNearbyStudyHallsWithDB(request);
        }
    }

    /**
     * Redis Trie를 활용한 스마트 자동완성 (성능 측정 제거)
     */
    public List<String> getSmartAutocompleteSuggestions(String prefix, int limit) {
        try {
            if (StringUtils.hasText(prefix)) {
                autoCompleteService.recordSearch(prefix);
            }

            return autoCompleteService.getAutocompleteSuggestions(prefix, limit);

        } catch (Exception e) {
            log.debug("Redis 자동완성 실패, 기본 자동완성으로 Fallback");
            return getFallbackAutocompleteSuggestions(prefix, limit);
        }
    }

    /**
     * 통합 검색 - Redis + DB 하이브리드
     */
    public List<StudyHallLocationResponse> hybridSearch(StudyHallSearchRequest request) {
        if (request.hasValidLocationInfo()) {
            return searchNearbyStudyHallsWithRedis(request);
        }

        if (request.hasValidKeyword()) {
            return searchByKeywordWithDB(request);
        }

        return getAllStudyHallsFromDB(request.getLimit());
    }

    // === Private Helper Methods ===

    private List<StudyHallLocationResponse> searchNearbyStudyHallsWithDB(StudyHallSearchRequest request) {
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

    private List<StudyHallLocationResponse> searchByKeywordWithDB(StudyHallSearchRequest request) {
        String keyword = request.getKeyword();
        Integer limit = request.getLimit();

        // 1차: 이름으로 검색
        List<StudyHall> nameResults = studyHallRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(keyword);

        // 2차: 주소로 검색
        List<StudyHall> addressResults = studyHallRepository
                .findByLocationAddressContainingIgnoreCase(keyword);

        Set<StudyHall> combinedResults = new LinkedHashSet<>(nameResults);
        combinedResults.addAll(addressResults);

        return combinedResults.stream()
                .limit(Objects.requireNonNullElse(limit, 50))
                .map(StudyHallLocationResponse::from)
                .collect(Collectors.toList());
    }

    private List<StudyHallLocationResponse> getAllStudyHallsFromDB(Integer limit) {
        Pageable pageable = PageRequest.of(0, Objects.requireNonNullElse(limit, 50));
        Page<StudyHall> page = studyHallRepository.findAllByIsDeletedFalse(pageable);

        return page.getContent().stream()
                .map(StudyHallLocationResponse::from)
                .collect(Collectors.toList());
    }

    private List<String> getFallbackAutocompleteSuggestions(String prefix, int limit) {
        if (!StringUtils.hasText(prefix) || prefix.length() < 2) {
            return Collections.emptyList();
        }

        List<StudyHall> halls = studyHallRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(prefix);

        return halls.stream()
                .limit(limit)
                .map(StudyHall::getName)
                .collect(Collectors.toList());
    }

    private List<StudyHallLocationResponse> applyAdditionalFilters(
            List<StudyHallLocationResponse> results,
            StudyHallSearchRequest request) {

        // TODO: 추가 필터링 로직 (용량, 운영시간 등)
        return results.stream()
                .filter(hall -> matchesCapacityFilter(hall, request))
                .collect(Collectors.toList());
    }

    private boolean matchesCapacityFilter(StudyHallLocationResponse hall, StudyHallSearchRequest request) {
        // TODO: 용량 필터링 로직 구현
        return true;
    }

    private String determineSearchType(StudyHallSearchRequest request) {
        if (request.hasValidLocationInfo()) {
            return "REDIS_GEO";
        } else if (request.hasValidKeyword()) {
            return "DB_KEYWORD";
        } else {
            return "DB_ALL";
        }
    }

    private boolean isCacheHit(String searchType, long duration) {
        // Redis나 캐시를 사용한 경우 일반적으로 50ms 이하
        return (searchType.contains("REDIS") && duration < 50) || duration < 20;
    }

    private BoundingBox calculateBoundingBox(StudyHallSearchRequest request) {
        double kmPerDegreeLat = 111.0;
        double kmPerDegreeLng = 111.0 * Math.cos(Math.toRadians(request.getLatitude()));

        double deltaLat = request.getRadius() / kmPerDegreeLat;
        double deltaLng = request.getRadius() / kmPerDegreeLng;

        return BoundingBox.builder()
                .minLat(request.getLatitude() - deltaLat)
                .maxLat(request.getLatitude() + deltaLat)
                .minLng(request.getLongitude() - deltaLng)
                .maxLng(request.getLongitude() + deltaLng)
                .build();
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

    // Inner Classes
    @lombok.Builder
    @lombok.Getter
    private static class BoundingBox {
        private final double minLat;
        private final double maxLat;
        private final double minLng;
        private final double maxLng;
    }
}