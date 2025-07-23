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
     * 현재 위치 기준으로 일정 반경 내의 스터디홀 조회 (거리 계산 포함)
     * MySQL의 ST_Distance_Sphere 함수를 사용하여 구면 거리 계산
     *
     * @param latitude 현재 위치 위도
     * @param longitude 현재 위치 경도
     * @param radiusInMeters 검색 반경 (미터)
     * @param limit 결과 개수 제한
     * @return 거리 순으로 정렬된 스터디홀 목록
     */
    @Query(value = """
        SELECT sh.*, 
               (ST_Distance_Sphere(
                   POINT(sh.longitude, sh.latitude), 
                   POINT(:longitude, :latitude)
               ) / 1000) as distance_km
        FROM study_hall sh 
        WHERE ST_Distance_Sphere(
                  POINT(sh.longitude, sh.latitude), 
                  POINT(:longitude, :latitude)
              ) <= :radiusInMeters
        ORDER BY distance_km ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNearbyStudyHallsWithDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusInMeters") Double radiusInMeters,
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
    List<StudyHall> findByAddressContainingIgnoreCase(String address);
}