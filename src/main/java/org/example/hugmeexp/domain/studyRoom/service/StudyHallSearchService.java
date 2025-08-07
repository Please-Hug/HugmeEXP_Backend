package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomConstants;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.projection.StudyHallWithDistanceProjection;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.service.redis.RedisAutoCompleteService;
import org.example.hugmeexp.domain.studyRoom.service.redis.RedisGeoService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
     * Redis Geo를 활용한 위치 기반 검색
     */
    @Cacheable(value = "nearbyHallsRedis", key = "#request.cacheKey", unless = "#result.isEmpty()")
    public List<StudyHallLocationResponse> searchNearbyStudyHallsWithRedis(StudyHallSearchRequest request) {
        try {
            List<StudyHallLocationResponse> redisResults = redisGeoService.findNearbyStudyHalls(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadius(),
                    request.getLimit()
            );

            if (!redisResults.isEmpty()) {
                log.debug("Redis Geo 검색 성공 - 캐시 키: {}", request.getCacheKey());
                return redisResults;
            }

            // Redis에 데이터가 없으면 DB 검색 (Fallback)
            return searchNearbyStudyHallsWithDB(request);

        } catch (Exception e) {
            log.debug("Redis Geo 검색 실패, DB 검색으로 Fallback - 캐시 키: {}", request.getCacheKey());
            return searchNearbyStudyHallsWithDB(request);
        }
    }

    /**
     * Redis Trie를 활용한 스마트 자동완성
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

    public List<StudyHallLocationResponse> searchNearbyStudyHalls(StudyHallSearchRequest request) {
        try {
            List<StudyHallLocationResponse> redisResults = redisGeoService.findNearbyStudyHalls(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadius(),
                    request.getLimit()
            );

            if (!redisResults.isEmpty()) {
                return redisResults;
            }
        } catch (Exception e) {
            log.debug("Redis Geo 검색 실패, DB 검색으로 fallback");
        }

        // DB Fallback
        return searchNearbyStudyHallsWithDB(request);
    }

    public List<StudyHallLocationResponse> searchStudyHallsByAddress(String address) {
        List<StudyHall> studyHalls = studyHallRepository.findByLocationAddressContainingIgnoreCase(address);
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    public List<StudyHallLocationResponse> searchStudyHallsByName(String name) {
        // 검색어 기록
        try {
            autoCompleteService.recordSearch(name);
        } catch (Exception e) {
            log.debug("Redis 검색어 기록 실패");
        }

        List<StudyHall> studyHalls = studyHallRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    // DB 기반 주변 검색 (Fallback용)
    private List<StudyHallLocationResponse> searchNearbyStudyHallsWithDB(StudyHallSearchRequest request) {
        StudyHallSearchService.BoundingBox boundingBox = calculateBoundingBox(request);

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

    // === Private Helper Methods ===

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

    /**
     * 바운딩박스
     * 주어진 위도, 경도, 반경을 기반으로 바운딩 박스를 계산합니다.
     */
    private BoundingBox calculateBoundingBox(StudyHallSearchRequest request) {
        double kmPerDegreeLng = StudyRoomConstants.KM_PER_DEGREE_LAT * Math.cos(Math.toRadians(request.getLatitude()));

        double deltaLat = request.getRadius() / StudyRoomConstants.KM_PER_DEGREE_LAT;
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