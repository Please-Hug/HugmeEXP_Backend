package org.example.hugmeexp.domain.studyRoom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.entity.Location;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.dto.mapper.StudyHallMapper;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
/**
 * 회의실(스터디 홀) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyHallService {

    private final StudyHallRepository studyHallRepository;

    /**
     * 새로운 회의실(스터디 홀)을 생성하고 데이터베이스에 저장합니다.
     * @param requestDto 회의실 생성을 위한 데이터가 담긴 DTO
     * @return 데이터베이스에 저장된 StudyHall 엔티티
     */
    @Transactional
    public StudyHall createStudyHall(StudyHallRequest requestDto) {
        StudyHall studyHall = StudyHall.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .simpleAddress(requestDto.getSimpleAddress())
                .address(requestDto.getAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .thumbnail(requestDto.getThumbnail())
                .openTime(requestDto.getOpenTime())
                .closeTime(requestDto.getCloseTime())
                .build();

        return studyHallRepository.save(studyHall);
    }

    /**
     * 등록된 모든 스터디 홀 목록을 조회합니다.
     * @return StudyHall 엔티티 리스트
     */
    public Page<StudyHall> findAllStudyHalls(Pageable pageable) {
        return studyHallRepository.findAllByIsDeletedFalse(pageable);
    }

    /**
     * 특정 ID로 스터디 홀을 조회합니다.
     * @param studyHallId 조회할 스터디 홀의 ID
     * @return 찾아낸 StudyHall 엔티티
     * @throws StudyHallNotFoundException 해당 ID의 홀이 없을 경우
     */
    public StudyHall findStudyHallById(Long studyHallId) {
        return studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));
    }

    /**
     * 특정 스터디 홀의 정보를 수정합니다.
     * @param studyHallId 수정할 스터디 홀의 ID
     * @param requestDto 수정할 정보가 담긴 DTO
     * @return 수정된 StudyHall 엔티티
     */
    @Transactional
    public StudyHall updateStudyHall(Long studyHallId, StudyHallRequest requestDto) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.update(requestDto);
        return studyHall;
    }

    /**
     * 특정 스터디 홀을 삭제합니다.
     * @param studyHallId 삭제할 스터디 홀의 ID
     */
    @Transactional
    public void deleteStudyHall(Long studyHallId) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.delete();
    }
    private final KakaoMapService kakaoMapService;

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
     * 현재 위치 기반 주변 스터디홀 검색
     */
    public List<StudyHallLocationResponse> searchNearbyStudyHalls(StudyHallSearchRequest request) {
        log.info("Searching nearby study halls - lat: {}, lng: {}, radius: {}km",
                request.getLatitude(), request.getLongitude(), request.getRadius());

        // 반경을 미터로 변환
        Double radiusInMeters = request.getRadius() * 1000;

        // 데이터베이스에서 거리 기반 검색
        List<Object[]> results = studyHallRepository.findNearbyStudyHallsWithDistance(
                request.getLatitude(),
                request.getLongitude(),
                radiusInMeters,
                request.getLimit()
        );

        List<StudyHallLocationResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            try {
                // Native query 결과를 StudyHall 객체로 변환
                StudyHall studyHall = mapResultToStudyHall(result);
                Double distance = ((Number) result[result.length - 1]).doubleValue(); // 마지막 컬럼이 distance_km

                StudyHallLocationResponse response = StudyHallLocationResponse.from(studyHall, distance);
                responses.add(response);

            } catch (Exception e) {
                log.error("Error mapping study hall result", e);
            }
        }

        log.info("Found {} nearby study halls", responses.size());
        return responses;
    }

    /**
     * 특정 스터디홀 상세 정보 조회
     */
    public StudyHallLocationResponse getStudyHallDetail(Long studyHallId) {
        StudyHall studyHall = studyHallRepository.findByIdWithStudyRooms(studyHallId);
        if (studyHall == null) {
            throw new StudyHallNotFoundException();
        }

        return StudyHallLocationResponse.from(studyHall);
    }

    /**
     * 현재 위치로부터 특정 스터디홀까지의 거리 계산
     */
    public StudyHallLocationResponse getStudyHallWithDistance(Long studyHallId, Double currentLat, Double currentLng) {
        StudyHall studyHall = studyHallRepository.findByIdWithStudyRooms(studyHallId);
        if (studyHall == null) {
            throw new StudyHallNotFoundException();
        }

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
        List<StudyHall> studyHalls = studyHallRepository.findByAddressContainingIgnoreCase(address);
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    /**
     * 이름으로 스터디홀 검색
     */
    public List<StudyHallLocationResponse> searchStudyHallsByName(String name) {
        List<StudyHall> studyHalls = studyHallRepository.findByNameContainingIgnoreCase(name);
        return studyHalls.stream()
                .map(StudyHallLocationResponse::from)
                .toList();
    }

    /**
     * 주소를 좌표로 변환하여 스터디홀 생성 시 사용
     */
    @Transactional
    public Location convertAddressToLocation(String address) {
        return kakaoMapService.addressToCoordinates(address);
    }

    /**
     * Native query 결과를 StudyHall 엔티티로 매핑
     */
    private StudyHall mapResultToStudyHall(Object[] result) {
        return StudyHall.builder()
                .id(((BigInteger) result[0]).longValue())
                .name((String) result[3])
                .description((String) result[4])
                .simpleAddress((String) result[5])
                .address((String) result[6])
                .latitude((Double) result[7])
                .longitude((Double) result[8])
                .thumbnail((String) result[9])
                .openTime(result[10] != null ? ((Timestamp) result[10]).toLocalDateTime() : null)
                .closeTime(result[11] != null ? ((Timestamp) result[11]).toLocalDateTime() : null)
                .build();
    }
}