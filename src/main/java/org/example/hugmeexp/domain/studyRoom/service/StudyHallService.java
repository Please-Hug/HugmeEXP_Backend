package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.Location;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.exception.LocationServiceException;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.projection.StudyHallWithDistanceProjection;
import org.example.hugmeexp.domain.studyRoom.reids.event.StudyHallCreatedEvent;
import org.example.hugmeexp.domain.studyRoom.reids.event.StudyHallDeletedEvent;
import org.example.hugmeexp.domain.studyRoom.reids.event.StudyHallUpdatedEvent;
import org.example.hugmeexp.domain.studyRoom.reids.service.RedisAutoCompleteService;
import org.example.hugmeexp.domain.studyRoom.reids.service.RedisGeoService;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.*;
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
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 새로운 회의실(스터디 홀)을 생성하고 reids에 동기화하여 데이터베이스에 저장합니다.
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

        // 🚀 Redis 동기화 이벤트 발행
        eventPublisher.publishEvent(new StudyHallCreatedEvent(savedHall));

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

        // 🚀 Redis 동기화 이벤트 발행
        eventPublisher.publishEvent(new StudyHallUpdatedEvent(updatedHall));

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

        // 🚀 Redis 정리 이벤트 발행
        eventPublisher.publishEvent(new StudyHallDeletedEvent(studyHallId));
    }

    // 기존 메소드들은 그대로 유지
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
                .map(this::convertToLocationResponse)
                .toList();
    }

    /**
     * Redis Geo를 우선 사용하는 주변 검색
     */
    public List<StudyHallLocationResponse> searchNearbyStudyHalls(StudyHallSearchRequest request) {
        log.info("주변 스터디홀 검색 - Redis Geo 우선 시도");

        try {
            // 1차: Redis Geo 검색 시도
            List<StudyHallLocationResponse> redisResults = redisGeoService.findNearbyStudyHalls(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadius(),
                    request.getLimit()
            );

            if (!redisResults.isEmpty()) {
                log.info("Redis Geo 검색 성공: {} 개", redisResults.size());
                return redisResults;
            }

            log.warn("Redis Geo 데이터 없음, DB 검색으로 fallback");

        } catch (Exception e) {
            log.error("Redis Geo 검색 실패, DB 검색으로 fallback", e);
        }

        // 2차: 기존 DB 검색 (Fallback)
        return searchNearbyStudyHallsWithDB(request);
    }

    /**
     * Redis Trie 자동완성
     */
    public List<String> getSmartAutocompleteSuggestions(String prefix, int limit) {
        try {
            return autoCompleteService.getAutocompleteSuggestions(prefix, limit);
        } catch (Exception e) {
            log.error("Redis 자동완성 실패, DB fallback", e);
            return getBasicAutocompleteSuggestions(prefix, limit);
        }
    }

    /**
     * 기존 DB 기반 검색 메소드들 (Fallback용)
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

    private List<String> getBasicAutocompleteSuggestions(String prefix, int limit) {
        if (!StringUtils.hasText(prefix) || prefix.length() < 2) {
            return Collections.emptyList();
        }

        List<StudyHall> halls = studyHallRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(prefix);

        Set<String> suggestions = new LinkedHashSet<>();

        for (StudyHall hall : halls) {
            if (StringUtils.hasText(hall.getName()) &&
                    hall.getName().toLowerCase().contains(prefix.toLowerCase())) {
                suggestions.add(hall.getName());
            }

            if (StringUtils.hasText(hall.getSimpleAddress()) &&
                    hall.getSimpleAddress().toLowerCase().contains(prefix.toLowerCase())) {
                suggestions.add(hall.getSimpleAddress());
            }

            if (suggestions.size() >= limit) break;
        }

        return new ArrayList<>(suggestions);
    }

    // === 기존 검색 메소드들 유지 ===

    public StudyHallLocationResponse getStudyHallDetail(Long studyHallId) {
        StudyHall studyHall = studyHallRepository.findByIdWithStudyRooms(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        return convertToLocationResponse(studyHall);
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
                .map(this::convertToLocationResponse)
                .toList();
    }

    public List<StudyHallLocationResponse> searchStudyHallsByName(String name) {
        // 검색어 기록 (인기도 증가)
        autoCompleteService.recordSearch(name);

        List<StudyHall> studyHalls = studyHallRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
        return studyHalls.stream()
                .map(this::convertToLocationResponse)
                .toList();
    }

    public Location convertAddressToLocation(String address) {
        try {
            Optional<Location> locationOpt = kakaoMapService.addressToCoordinates(address);
            return locationOpt.orElseThrow(() ->
                    new LocationServiceException("주소를 좌표로 변환할 수 없습니다: " + address));
        } catch (Exception e) {
            log.error("Error mapping study hall result", e);
            throw new LocationServiceException("주소 변환 중 오류가 발생했습니다: " + address);
        }
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

    private StudyHallLocationResponse convertToLocationResponse(StudyHall studyHall) {
        return StudyHallLocationResponse.from(studyHall);
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