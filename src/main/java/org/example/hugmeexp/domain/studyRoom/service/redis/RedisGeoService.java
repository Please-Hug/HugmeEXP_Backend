package org.example.hugmeexp.domain.studyRoom.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomConstants;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Redis Geo 서비스: 위치 기반 검색 전용
 *
 * 🎯 사용 범위: 지도 검색, 주변 스터디홀 찾기
 * 🔧 사용 Redis Bean: geoRedisTemplate (위치 데이터 최적화)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGeoService {

    @Qualifier("geoRedisTemplate") // Geo 전용 RedisTemplate 사용
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GEO_KEY = "studyhalls:locations";
    private static final String HALL_DATA_KEY = "studyhalls:data:";

    /**
     * 스터디홀 위치 정보를 Redis Geo에 저장
     */
    public void indexStudyHallLocation(StudyHall studyHall) {
        try {
            Double lat = studyHall.getLatitude();
            Double lng = studyHall.getLongitude();

            if (lat == null || lng == null) {
                log.warn("스터디홀 {}의 위치 정보가 없습니다.", studyHall.getId());
                return;
            }

            // 한국 지역 범위 검증
            if (lat < StudyRoomConstants.KOREA_MIN_LATITUDE || lat > StudyRoomConstants.KOREA_MAX_LATITUDE ||
                    lng < StudyRoomConstants.KOREA_MIN_LONGITUDE || lng > StudyRoomConstants.KOREA_MAX_LONGITUDE) {
                log.warn("스터디홀 {}의 위치가 유효 범위를 벗어났습니다: lat={}, lng={}", studyHall.getId(), lat, lng);
                return;
            }

            // Geo 정보 저장
            Point location = new Point(lng, lat);
            redisTemplate.opsForGeo().add(GEO_KEY, location, studyHall.getId().toString());

            // 상세 정보 별도 저장 (Hash 구조) - 위치 관련 데이터만
            Map<String, Object> hallData = Map.of(
                    "id", studyHall.getId(),
                    "name", studyHall.getName(),
                    "simpleAddress", studyHall.getSimpleAddress() != null ? studyHall.getSimpleAddress() : "",
                    "thumbnail", studyHall.getThumbnail() != null ? studyHall.getThumbnail() : "",
                    "totalRooms", studyHall.getTotalRoomsCount()
            );

            redisTemplate.opsForHash().putAll(HALL_DATA_KEY + studyHall.getId(), hallData);

            log.debug("스터디홀 {} 위치 정보 저장 완료: lat={}, lng={}", studyHall.getId(), lat, lng);

        } catch (Exception e) {
            log.error("스터디홀 {} 위치 정보 저장 실패", studyHall.getId(), e);
        }
    }

    /**
     * 반경 내 스터디홀 검색 (Redis Geo 활용)
     */
    public List<StudyHallLocationResponse> findNearbyStudyHalls(Double lat, Double lng, Double radiusKm, Integer limit) {
        try {
            // Redis Geo 검색
            Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
            Circle searchArea = new Circle(new Point(lng, lat), radius);

            GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults =
                    redisTemplate.opsForGeo().radius(GEO_KEY, searchArea,
                            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                    .includeDistance()
                                    .includeCoordinates()
                                    .sortAscending()
                                    .limit(limit != null ? limit : 50));

            if (geoResults == null) {
                return Collections.emptyList();
            }

            // 결과 변환
            List<StudyHallLocationResponse> results = new ArrayList<>();

            for (GeoResult<RedisGeoCommands.GeoLocation<Object>> result : geoResults) {
                String hallId = result.getContent().getName().toString();
                Double distance = result.getDistance().getValue();
                Point coordinates = result.getContent().getPoint();

                // Redis에서 상세 정보 조회
                Map<Object, Object> hallData = redisTemplate.opsForHash().entries(HALL_DATA_KEY + hallId);

                if (!hallData.isEmpty()) {
                    StudyHallLocationResponse response = StudyHallLocationResponse.builder()
                            .id(Long.parseLong(hallId))
                            .name((String) hallData.get("name"))
                            .simpleAddress((String) hallData.get("simpleAddress"))
                            .latitude(coordinates.getY())
                            .longitude(coordinates.getX())
                            .thumbnail((String) hallData.get("thumbnail"))
                            .distance(Math.round(distance * 100.0) / 100.0) // 소수점 2자리
                            .totalRooms(hallData.get("totalRooms") != null ?
                                    Integer.parseInt(hallData.get("totalRooms").toString()) : 0)
                            .build();

                    results.add(response);
                }
            }

            log.info("Redis Geo 검색 결과: {}개 (반경 {}km)", results.size(), radiusKm);
            return results;

        } catch (Exception e) {
            log.error("Redis Geo 검색 실패 - lat: {}, lng: {}, radius: {}km", lat, lng, radiusKm, e);
            return Collections.emptyList();
        }
    }

    /**
     * 스터디홀 위치 정보 삭제
     */
    public void removeStudyHallLocation(Long studyHallId) {
        try {
            redisTemplate.opsForGeo().remove(GEO_KEY, studyHallId.toString());
            redisTemplate.delete(HALL_DATA_KEY + studyHallId);
            log.debug("스터디홀 {} 위치 정보 삭제 완료", studyHallId);
        } catch (Exception e) {
            log.error("스터디홀 {} 위치 정보 삭제 실패", studyHallId, e);
        }
    }

    /**
     * 모든 스터디홀 위치 정보 재색인
     */
    public void reindexAllStudyHalls(List<StudyHall> studyHalls) {
        try {
            // 기존 데이터 삭제
            redisTemplate.delete(GEO_KEY);

            // 기존 Hash 데이터도 모두 삭제
            Set<String> hashKeys = redisTemplate.keys(HALL_DATA_KEY + "*");
            if (hashKeys != null && !hashKeys.isEmpty()) {
                redisTemplate.delete(hashKeys);
            }

            // 새 데이터 색인
            for (StudyHall hall : studyHalls) {
                indexStudyHallLocation(hall);
            }

            log.info("전체 스터디홀 {} 개 재색인 완료", studyHalls.size());
        } catch (Exception e) {
            log.error("스터디홀 재색인 실패", e);
        }
    }
}