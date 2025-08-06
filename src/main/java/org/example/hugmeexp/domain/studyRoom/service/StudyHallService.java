package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.Location;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.projection.StudyHallWithDistanceProjection;
import org.example.hugmeexp.domain.studyRoom.reids.service.RedisAutoCompleteService;
import org.example.hugmeexp.domain.studyRoom.reids.service.RedisGeoService;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudyHallService {

    private final StudyHallRepository studyHallRepository;
    private final KakaoMapService kakaoMapService;
    private final RedisGeoService redisGeoService;
    private final RedisAutoCompleteService autoCompleteService;

    /**
     * 새로운 스터디 홀을 생성하고 Redis에 자동 동기화
     */
    @Transactional
    public StudyHall createStudyHall(StudyHallRequest requestDto) {
        validateCoordinates(requestDto.getLatitude(), requestDto.getLongitude());

        Location location = Location.of(
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getAddress(),
                requestDto.getSimpleAddress()
        );

        StudyHall studyHall = StudyHall.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .location(location)
                .thumbnail(requestDto.getThumbnail())
                .openTime(requestDto.getOpenTime())
                .closeTime(requestDto.getCloseTime())
                .build();

        StudyHall savedHall = studyHallRepository.save(studyHall);

        // Redis 동기화 (조용히 처리)
        syncToRedis(savedHall);

        return savedHall;
    }

    /**
     * 스터디 홀 정보 수정 시 Redis도 함께 업데이트
     */
    @Transactional
    @CacheEvict(value = "studyHalls", key = "#studyHallId")
    public StudyHall updateStudyHall(Long studyHallId, StudyHallRequest requestDto) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.update(requestDto);

        StudyHall updatedHall = studyHallRepository.save(studyHall);

        // Redis 동기화 (조용히 처리)
        syncToRedis(updatedHall);

        return updatedHall;
    }

    /**
     * 스터디 홀 삭제 시 Redis에서도 제거
     */
    @Transactional
    @CacheEvict(value = "studyHalls", key = "#studyHallId")
    public void deleteStudyHall(Long studyHallId) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.delete();

        // Redis에서 제거 (조용히 처리)
        removeFromRedis(studyHallId);
    }

    // === Redis 동기화 (조용한 처리) ===

    private void syncToRedis(StudyHall studyHall) {
        try {
            redisGeoService.indexStudyHallLocation(studyHall);
            autoCompleteService.indexSearchTerms(studyHall);
        } catch (Exception e) {
            // Redis 실패해도 DB는 정상 처리됨, 조용히 로그만
            log.debug("Redis 동기화 실패 (DB는 정상 처리됨) - StudyHall: {}", studyHall.getId());
        }
    }

    private void removeFromRedis(Long studyHallId) {
        try {
            redisGeoService.removeStudyHallLocation(studyHallId);
        } catch (Exception e) {
            log.debug("Redis 삭제 실패 (DB는 정상 처리됨) - StudyHall: {}", studyHallId);
        }
    }

    // === 기본 CRUD 메소드들 ===

    public Page<StudyHall> findAllStudyHalls(Pageable pageable) {
        return studyHallRepository.findAllByIsDeletedFalse(pageable);
    }

    @Cacheable(value = "studyHalls", key = "#studyHallId")
    public StudyHall findStudyHallById(Long studyHallId) {
        return studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));
    }

    public List<StudyHallLocationResponse> getAllStudyHallsForMap() {
        List<StudyHall> studyHalls = studyHallRepository.findAllWithStudyRooms();
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    // === Redis 기반 검색 메소드들 ===

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

    public List<String> getSmartAutocompleteSuggestions(String prefix, int limit) {
        try {
            return autoCompleteService.getAutocompleteSuggestions(prefix, limit);
        } catch (Exception e) {
            log.debug("Redis 자동완성 실패, DB fallback");
            return getBasicAutocompleteSuggestions(prefix, limit);
        }
    }

    // === StudyRoomMapController에서 호출하는 메서드들 ===

    public StudyHallLocationResponse getStudyHallDetail(Long studyHallId) {
        StudyHall studyHall = studyHallRepository.findByIdWithStudyRooms(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        return StudyHallLocationResponse.from(studyHall);
    }

    public StudyHallLocationResponse getStudyHallWithDistance(Long studyHallId, Double currentLat, Double currentLng) {
        StudyHall studyHall = studyHallRepository.findByIdWithStudyRooms(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        Double distance = kakaoMapService.calculateDistance(
                currentLat, currentLng,
                studyHall.getLatitude(), studyHall.getLongitude()
        );

        return StudyHallLocationResponse.from(studyHall, distance);
    }

    public List<StudyHallLocationResponse> searchStudyHallsByAddress(String address) {
        List<StudyHall> studyHalls = studyHallRepository.findByLocationAddressContainingIgnoreCase(address);
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    public List<StudyHallLocationResponse> searchStudyHallsByName(String name) {
        // 검색어 기록 (인기도 증가)
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

    // === Private Helper Methods ===

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("위도는 -90에서 90 사이여야 합니다.");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("경도는 -180에서 180 사이여야 합니다.");
        }
    }

    /**
     * DB 기반 주변 검색 (Fallback용)
     */
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

    /**
     * 기본 자동완성 제안 (2글자 이상)
     */
    private List<String> getBasicAutocompleteSuggestions(String prefix, int limit) {
        if (!StringUtils.hasText(prefix) || prefix.length() < 2) {
            return Collections.emptyList();
        }

        List<StudyHall> halls = studyHallRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(prefix);

        Set<String> suggestions = ConcurrentHashMap.newKeySet();

        String lowerPrefix = prefix.toLowerCase();

        for (StudyHall hall : halls) {
            if (StringUtils.hasText(hall.getName()) &&
                    hall.getName().toLowerCase().contains(lowerPrefix)) {
                suggestions.add(hall.getName());
            }

            if (StringUtils.hasText(hall.getSimpleAddress()) &&
                    hall.getSimpleAddress().toLowerCase().contains(lowerPrefix)) {
                suggestions.add(hall.getSimpleAddress());
            }

            if (suggestions.size() >= limit) break;
        }

        return new ArrayList<>(suggestions);
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

    @lombok.Builder
    @lombok.Getter
    private static class BoundingBox {
        private final double minLat;
        private final double maxLat;
        private final double minLng;
        private final double maxLng;
    }
}