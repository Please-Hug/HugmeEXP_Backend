package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomConstants;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.Location;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.projection.StudyHallWithDistanceProjection;
import org.example.hugmeexp.domain.studyRoom.service.redis.RedisAutoCompleteService;
import org.example.hugmeexp.domain.studyRoom.service.redis.RedisGeoService;
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

        StudyHall updatedHall = studyHall;

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

        // Redis에서 제거
        removeFromRedis(studyHallId);
    }

    //Redis 동기화
    private void syncToRedis(StudyHall studyHall) {
        try {
            if (studyHall != null && studyHall.getId() != null) {
                redisGeoService.indexStudyHallLocation(studyHall);
                autoCompleteService.indexSearchTerms(studyHall);
            }
        } catch (Exception e) {
            // Redis 실패해도 DB는 정상 처리됨, 조용히 로그만
            log.debug("Redis 동기화 실패 (DB는 정상 처리됨) - StudyHall: {}",
                    studyHall != null ? studyHall.getId() : "null");
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



    // === Private Helper Methods ===

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude != null && (latitude < StudyRoomConstants.KOREA_MIN_LATITUDE || latitude > StudyRoomConstants.KOREA_MAX_LATITUDE)) {
            throw new IllegalArgumentException("위도는 " + StudyRoomConstants.KOREA_MIN_LATITUDE + "에서 " + StudyRoomConstants.KOREA_MAX_LATITUDE + " 사이여야 합니다.");
        }
        if (longitude != null && (longitude < StudyRoomConstants.KOREA_MIN_LONGITUDE || longitude > StudyRoomConstants.KOREA_MAX_LONGITUDE)) {
            throw new IllegalArgumentException("경도는 " + StudyRoomConstants.KOREA_MIN_LONGITUDE + "에서 " + StudyRoomConstants.KOREA_MAX_LONGITUDE + " 사이여야 합니다.");
        }
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


}