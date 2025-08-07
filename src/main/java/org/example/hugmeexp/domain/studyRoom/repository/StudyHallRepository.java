package org.example.hugmeexp.domain.studyRoom.repository;

import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.projection.StudyHallWithDistanceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyHallRepository extends JpaRepository<StudyHall, Long> {

    /**
     * 삭제되지 않은 모든 스터디홀 조회 (페이징)
     */
    Page<StudyHall> findAllByIsDeletedFalse(Pageable pageable);

    /**
     * 삭제되지 않은 특정 스터디홀 조회
     */
    Optional<StudyHall> findByIdAndIsDeletedFalse(Long id);

    /**
     * 주변 스터디홀 검색, 인덱스 하드코딩을 피하기 위해 jpql의 constructor expression을 사용하여 DTO로 반환
     */
    @Query(value = """
    SELECT sh.studyhall_id as id,
           sh.name as name,
           sh.description as description,
           sh.latitude as latitude,
           sh.longitude as longitude,
           sh.address as address,
           sh.simple_address as simpleAddress,
           sh.thumbnail_url as thumbnail,
           sh.open_time as openTime,
           sh.close_time as closeTime,
           (6371 * ACOS(
               GREATEST(-1, LEAST(1,
                   COS(RADIANS(:latitude)) * 
                   COS(RADIANS(sh.latitude)) * 
                   COS(RADIANS(sh.longitude) - RADIANS(:longitude)) + 
                   SIN(RADIANS(:latitude)) * 
                   SIN(RADIANS(sh.latitude))
               ))
           )) as distance
    FROM study_hall sh 
    WHERE sh.latitude BETWEEN :minLat AND :maxLat
      AND sh.longitude BETWEEN :minLng AND :maxLng
      AND sh.latitude IS NOT NULL 
      AND sh.longitude IS NOT NULL
      AND sh.is_deleted = false
    HAVING distance <= :radiusInKm
    ORDER BY distance ASC
    LIMIT :limit
    """, nativeQuery = true)
    List<StudyHallWithDistanceProjection> findNearbyStudyHallsWithProjection(
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
    @Query("SELECT sh FROM StudyHall sh LEFT JOIN FETCH sh.studyRooms WHERE sh.isDeleted = false")
    List<StudyHall> findAllWithStudyRooms();

    /**
     * 특정 스터디홀 조회 (StudyRoom 정보 포함)
     */
    @Query("SELECT sh FROM StudyHall sh LEFT JOIN FETCH sh.studyRooms WHERE sh.id = :id AND sh.isDeleted = false")
    Optional<StudyHall> findByIdWithStudyRooms(@Param("id") Long id);

    /**
     * 이름으로 스터디홀 검색
     */
    List<StudyHall> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);

    /**
     * 주소로 스터디홀 검색
     */
    @Query("SELECT sh FROM StudyHall sh WHERE sh.location.address LIKE %:address% AND sh.isDeleted = false")
    List<StudyHall> findByLocationAddressContainingIgnoreCase(@Param("address") String address);

    /**
     * Redis 동기화용 - N+1 문제 해결을 위한 최적화된 조회
     * fetch join으로 한 번의 쿼리로 StudyHall과 StudyRoom을 모두 조회
     */
    @Query("SELECT sh FROM StudyHall sh " +
            "LEFT JOIN FETCH sh.studyRooms sr " +
            "WHERE sh.isDeleted = false")
    List<StudyHall> findAllWithStudyRoomsForSync();
}