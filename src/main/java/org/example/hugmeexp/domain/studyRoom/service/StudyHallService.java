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
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyHallService {

    private final StudyHallRepository studyHallRepository;
    private final KakaoMapService kakaoMapService;

    /**
     * 새로운 회의실(스터디 홀)을 생성하고 데이터베이스에 저장합니다.
     */
    @Transactional
    public StudyHall createStudyHall(StudyHallRequest requestDto) {

        // 좌표값 검증
        if (requestDto.getLatitude() != null && (requestDto.getLatitude() < -90 || requestDto.getLatitude() > 90)) {
            throw new IllegalArgumentException("위도는 -90에서 90 사이여야 합니다.");
        }
        if (requestDto.getLongitude() != null && (requestDto.getLongitude() < -180 || requestDto.getLongitude() > 180)) {
            throw new IllegalArgumentException("경도는 -180에서 180 사이여야 합니다.");
        }

        // Location 객체 생성
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

        return studyHallRepository.save(studyHall);
    }

    /**
     * 등록된 모든 스터디 홀 목록을 조회합니다.
     */
    public Page<StudyHall> findAllStudyHalls(Pageable pageable) {
        return studyHallRepository.findAllByIsDeletedFalse(pageable);
    }

    /**
     * 특정 ID로 스터디 홀을 조회합니다.
     */
    public StudyHall findStudyHallById(Long studyHallId) {
        return studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));
    }

    /**
     * 특정 스터디 홀의 정보를 수정합니다.
     */
    @Transactional
    public StudyHall updateStudyHall(Long studyHallId, StudyHallRequest requestDto) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.update(requestDto);
        return studyHall;
    }

    /**
     * 특정 스터디 홀을 삭제합니다.
     */
    @Transactional
    public void deleteStudyHall(Long studyHallId) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.delete();
    }

    /**
     * 모든 스터디홀 조회 (지도 표시용)
     */
    public List<StudyHallLocationResponse> getAllStudyHallsForMap() {
        List<StudyHall> studyHalls = studyHallRepository.findAllWithStudyRooms();
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    /**
     * 현재 위치 기반 주변 스터디홀 검색 (최적화된 Native Query 방식)
     */
    public List<StudyHallLocationResponse> searchNearbyStudyHalls(StudyHallSearchRequest request) {
        log.info("Searching nearby study halls - lat: {}, lng: {}, radius: {}km",
                request.getLatitude(), request.getLongitude(), request.getRadius());

        // 사각형 영역 계산 (성능 최적화용)
        double kmPerDegreeLat = 111.0;
        double kmPerDegreeLng = 111.0 * Math.cos(Math.toRadians(request.getLatitude()));

        double deltaLat = request.getRadius() / kmPerDegreeLat;
        double deltaLng = request.getRadius() / kmPerDegreeLng;

        double minLat = request.getLatitude() - deltaLat;
        double maxLat = request.getLatitude() + deltaLat;
        double minLng = request.getLongitude() - deltaLng;
        double maxLng = request.getLongitude() + deltaLng;

        // Interface Projection 사용
        List<StudyHallWithDistanceProjection> projections = studyHallRepository.findNearbyStudyHallsWithProjection(
                request.getLatitude(),
                request.getLongitude(),
                minLat, maxLat, minLng, maxLng,
                request.getRadius(),
                request.getLimit()
        );

        // Projection을 Response DTO로 변환
        List<StudyHallLocationResponse> responses = projections.stream()
                .map(this::convertProjectionToResponse)
                .collect(Collectors.toList());

        log.info("Found {} nearby study halls", responses.size());
        return responses;
    }

    /**
     * 특정 스터디홀 상세 정보 조회
     */
    public StudyHallLocationResponse getStudyHallDetail(Long studyHallId) {
        StudyHall studyHall = studyHallRepository.findByIdWithStudyRooms(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        return StudyHallLocationResponse.from(studyHall);
    }

    /**
     * 현재 위치로부터 특정 스터디홀까지의 거리 계산
     */
    public StudyHallLocationResponse getStudyHallWithDistance(Long studyHallId, Double currentLat, Double currentLng) {
        StudyHall studyHall = studyHallRepository.findByIdWithStudyRooms(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        Double distance = kakaoMapService.calculateDistance(
                currentLat, currentLng,
                studyHall.getLatitude(), studyHall.getLongitude()
        );

        return StudyHallLocationResponse.from(studyHall, distance);
    }

    /**
     * 주소로 스터디홀 검색
     */
    public List<StudyHallLocationResponse> searchStudyHallsByAddress(String address) {
        List<StudyHall> studyHalls = studyHallRepository.findByLocationAddressContainingIgnoreCase(address);
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    /**
     * 이름으로 스터디홀 검색
     */
    public List<StudyHallLocationResponse> searchStudyHallsByName(String name) {
        List<StudyHall> studyHalls = studyHallRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    /**
     * 주소를 좌표로 변환하여 스터디홀 생성 시 사용
     */
    @Transactional
    public Location convertAddressToLocation(String address) {
        Location location = kakaoMapService.addressToCoordinates(address);
        if (location == null) {
            throw new LocationServiceException("주소를 좌표로 변환할 수 없습니다: " + address);
        }
        return location;
    }

    /**
     * Native Query 결과(Interface Projection) → Response DTO로 변환
     */
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
                .totalRooms(0) // 기본값, 필요시 별도 조회
                .availableRooms(0) // 기본값, 필요시 별도 조회
                .build();
    }
}