package org.example.hugmeexp.domain.studyRoom.repository;

import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface StudyHallRepository extends JpaRepository<StudyHall, Long> {

    // 삭제되지 않은 홀 목록을 페이징하여 조회하는 메서드
    Page<StudyHall> findAllByIsDeletedFalse(Pageable pageable);

    // 삭제되지 않은 특정 홀만 조회하는 메서드
    Optional<StudyHall> findByIdAndIsDeletedFalse(Long studyHallId);

    /**
     * 성능 최적화된 주변 스터디홀 검색
     * 1. 사각형 영역으로 미리 필터링 (WHERE)
     * 2. 정확한 원형 거리 계산 (HAVING)
     */
    @Query(value = """
        SELECT * FROM (
            SELECT sh.*, 
                   (6371 * ACOS(
                       GREATEST(-1, LEAST(1,
                           COS(RADIANS(:latitude)) * 
                           COS(RADIANS(sh.latitude)) * 
                           COS(RADIANS(sh.longitude) - RADIANS(:longitude)) + 
                           SIN(RADIANS(:latitude)) * 
                           SIN(RADIANS(sh.latitude))
                       ))
                   )) as distance_km
            FROM study_hall sh 
            WHERE sh.latitude BETWEEN :minLat AND :maxLat
              AND sh.longitude BETWEEN :minLng AND :maxLng
              AND sh.latitude IS NOT NULL 
              AND sh.longitude IS NOT NULL
        ) t
        WHERE t.distance_km <= :radiusInKm
        ORDER BY t.distance_km ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNearbyStudyHallsOptimized(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng,
            @Param("radiusInKm") Double radiusInKm,
            @Param("limit") Integer limit
    );

    /**
     * 모든 스터디홀 조회 (StudyRoom 정보 포함)
     */
    @Query("SELECT sh FROM StudyHall sh LEFT JOIN FETCH sh.studyRooms")
    List<StudyHall> findAllWithStudyRooms();

    /**
     * 특정 스터디홀 조회 (StudyRoom 정보 포함)
     */
    @Query("SELECT sh FROM StudyHall sh LEFT JOIN FETCH sh.studyRooms WHERE sh.id = :id")
    StudyHall findByIdWithStudyRooms(@Param("id") Long id);

    /**
     * 이름으로 스터디홀 검색
     */
    List<StudyHall> findByNameContainingIgnoreCase(String name);

    /**
     * 주소로 스터디홀 검색
     */
    List<StudyHall> findByLocationAddressContainingIgnoreCase(String address);
}