package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    /**
     * 채용 공고 목록을 조회합니다.
     * 조건에 따라 필터링된 채용 공고를 페이지 단위로 반환합니다.
     *
     * @param cond 검색 조건 DTO
     * @param pageable 페이징 정보
     * @return 필터링된 채용 공고 목록 (RecruitmentResponseDTO)
     */
    @Query(
        value = """
            SELECT new org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO(
                r.id, r.recruitmentSourceId, r.title, c.companyName, c.companyImageUrl, r.dueDate,
                r.experienceMin, r.experienceMax, r.workLocation, r.latitude, r.longitude, r.modifiedAt
            )
            FROM Recruitment r
            JOIN r.company c
            LEFT JOIN r.techStacks ts
            LEFT JOIN r.tags t
            WHERE r.dueDate > CURRENT_TIMESTAMP AND
                (:#{#cond.salaryMin} IS NULL OR r.salaryMin >= :#{#cond.salaryMin}) AND
                (:#{#cond.salaryMax} IS NULL OR r.salaryMax <= :#{#cond.salaryMax}) AND
                (
                  (:#{#cond.experienceMin} IS NULL OR :#{#cond.experienceMax} IS NULL)
                  OR
                  (r.experienceMax >= :#{#cond.experienceMin} AND r.experienceMin <= :#{#cond.experienceMax})
                ) AND
                (:#{#cond.education} IS NULL OR r.education = :#{#cond.education}) AND
                (:#{#cond.workLocation} IS NULL OR r.workLocation LIKE %:#{#cond.workLocation}%) AND
                (
                    :#{#cond.topLeftLat} IS NULL OR 
                    :#{#cond.topLeftLng} IS NULL OR 
                    :#{#cond.bottomRightLat} IS NULL OR 
                    :#{#cond.bottomRightLng} IS NULL OR
                    (
                        r.latitude BETWEEN LEAST(:#{#cond.topLeftLat}, :#{#cond.bottomRightLat}) AND GREATEST(:#{#cond.topLeftLat}, :#{#cond.bottomRightLat}) AND
                        r.longitude BETWEEN LEAST(:#{#cond.topLeftLng}, :#{#cond.bottomRightLng}) AND GREATEST(:#{#cond.topLeftLng}, :#{#cond.bottomRightLng})
                    )
                ) AND
                (:#{#cond.techStacks} IS NULL OR ts.id IN :#{#cond.techStacks}) AND
                (:#{#cond.tags} IS NULL OR t.id IN :#{#cond.tags})
            GROUP BY r.id, r.recruitmentSourceId, r.title, c.companyName, c.companyImageUrl, r.dueDate,
                r.experienceMin, r.experienceMax, r.workLocation, r.latitude, r.longitude, r.modifiedAt
            HAVING (:#{#cond.techStacks} IS NULL OR COUNT(DISTINCT ts.id) = :#{#cond.techStackCount}) AND
                (:#{#cond.tags} IS NULL OR COUNT(DISTINCT t.id) = :#{#cond.tagCount})
            ORDER BY r.modifiedAt DESC
            """,
        countQuery = """
            SELECT COUNT(DISTINCT r.id)
            FROM Recruitment r
            LEFT JOIN r.techStacks ts
            LEFT JOIN r.tags t
            WHERE r.dueDate > CURRENT_TIMESTAMP AND
                (:#{#cond.salaryMin} IS NULL OR r.salaryMin >= :#{#cond.salaryMin}) AND
                (:#{#cond.salaryMax} IS NULL OR r.salaryMax <= :#{#cond.salaryMax}) AND
                (
                    (:#{#cond.experienceMin} IS NULL OR :#{#cond.experienceMax} IS NULL)
                     OR
                     (r.experienceMax >= :#{#cond.experienceMin} AND r.experienceMin <= :#{#cond.experienceMax})
                ) AND
                (:#{#cond.education} IS NULL OR r.education = :#{#cond.education}) AND
                (:#{#cond.workLocation} IS NULL OR r.workLocation LIKE %:#{#cond.workLocation}%) AND
                (
                    :#{#cond.topLeftLat} IS NULL OR 
                    :#{#cond.topLeftLng} IS NULL OR 
                    :#{#cond.bottomRightLat} IS NULL OR 
                    :#{#cond.bottomRightLng} IS NULL OR
                    (
                        r.latitude BETWEEN LEAST(:#{#cond.topLeftLat}, :#{#cond.bottomRightLat}) AND GREATEST(:#{#cond.topLeftLat}, :#{#cond.bottomRightLat}) AND
                        r.longitude BETWEEN LEAST(:#{#cond.topLeftLng}, :#{#cond.bottomRightLng}) AND GREATEST(:#{#cond.topLeftLng}, :#{#cond.bottomRightLng})
                    )
                ) AND
                (:#{#cond.techStacks} IS NULL OR ts.id IN :#{#cond.techStacks}) AND
                (:#{#cond.tags} IS NULL OR t.id IN :#{#cond.tags})
    """)
    Page<RecruitmentResponseDTO> findBySearchConditions(@Param("cond") RecruitmentSearchConditionDTO cond, Pageable pageable);


    /**
     * 최신 수정일 기준으로 정렬된 채용 공고 목록을 조회합니다.
     * Pageable을 사용하여 원하는 개수만큼 제한하여 가져옵니다.
     *
     * @param pageable 페이징 및 정렬 정보를 담은 객체
     * @return 최신 채용 공고 목록 (modifiedAt 기준 내림차순 정렬)
     */
    @Query("""
        SELECT new org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO(
            r.id, r.recruitmentSourceId, r.title, c.companyName, c.companyImageUrl, r.dueDate,
            r.experienceMin, r.experienceMax, r.workLocation, r.latitude, r.longitude, r.modifiedAt
        )
        FROM Recruitment r
        JOIN r.company c
        WHERE r.dueDate > CURRENT_TIMESTAMP
        ORDER BY r.modifiedAt DESC
    """)
    List<RecruitmentResponseDTO> findLatestRecruitments(Pageable pageable);

    /**
     * 채용 공고의 상세 정보를 ID로 조회합니다.
     * 회사 정보, 기술 스택, 태그 등을 포함하여 상세 정보를 반환합니다.
     *
     * @param id 채용 공고 ID
     * @return 채용 공고의 상세 정보 (존재하지 않을 경우 Optional.empty())
     */
    @Query("""
        SELECT DISTINCT r FROM Recruitment r
        JOIN FETCH r.company
        LEFT JOIN FETCH r.techStacks ts
        LEFT JOIN FETCH ts.techItem
        LEFT JOIN FETCH r.tags t
        LEFT JOIN FETCH t.tagItem
        WHERE r.id = :id
    """)
    Optional<Recruitment> findDetailById(@Param("id") Long id);

    /**
     * 키워드로 채용 공고를 검색합니다.
     * 제목 또는 회사 이름에 키워드가 포함된 채용 공고를 반환합니다.
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 키워드에 해당하는 채용 공고 목록
     */
    @Query("""
        SELECT r FROM Recruitment r
        JOIN FETCH r.company c
        WHERE r.dueDate > CURRENT_TIMESTAMP AND (
            LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    List<Recruitment> findByKeyword(@Param("keyword") String keyword, Pageable pageable);





    @Query("SELECT r FROM Recruitment r " +
            "LEFT JOIN FETCH r.techStacks ts " +
            "LEFT JOIN FETCH ts.techItem " +
            "LEFT JOIN FETCH r.tags t " +
            "LEFT JOIN FETCH t.tagItem " +
            "WHERE r.recruitmentSourceId = :recruitmentSourceId")
    Optional<Recruitment> findByRecruitmentSourceId(String recruitmentSourceId);
    
    /**
     * 검색 조건에 맞는 채용 공고의 ID 목록을 조회합니다.
     * 페이징 없이 모든 ID를 가져옵니다.
     *
     * @param cond 검색 조건 DTO
     * @return 필터링된 채용 공고 ID 목록
     */
    @Query(value = """
        SELECT r.id
        FROM Recruitment r
        LEFT JOIN r.techStacks ts
        LEFT JOIN r.tags t
        WHERE r.dueDate > CURRENT_TIMESTAMP AND
            (:#{#cond.salaryMin} IS NULL OR r.salaryMin >= :#{#cond.salaryMin}) AND
            (:#{#cond.salaryMax} IS NULL OR r.salaryMax <= :#{#cond.salaryMax}) AND
            (
              (:#{#cond.experienceMin} IS NULL OR :#{#cond.experienceMax} IS NULL)
              OR
              (r.experienceMax >= :#{#cond.experienceMin} AND r.experienceMin <= :#{#cond.experienceMax})
            ) AND
            (:#{#cond.education} IS NULL OR r.education = :#{#cond.education}) AND
            (:#{#cond.workLocation} IS NULL OR r.workLocation LIKE %:#{#cond.workLocation}%) AND
            (
                :#{#cond.topLeftLat} IS NULL OR 
                :#{#cond.topLeftLng} IS NULL OR 
                :#{#cond.bottomRightLat} IS NULL OR 
                :#{#cond.bottomRightLng} IS NULL OR
                (
                    r.latitude BETWEEN LEAST(:#{#cond.topLeftLat}, :#{#cond.bottomRightLat}) AND GREATEST(:#{#cond.topLeftLat}, :#{#cond.bottomRightLat}) AND
                    r.longitude BETWEEN LEAST(:#{#cond.topLeftLng}, :#{#cond.bottomRightLng}) AND GREATEST(:#{#cond.topLeftLng}, :#{#cond.bottomRightLng})
                )
            ) AND
            (:#{#cond.techStacks} IS NULL OR ts.id IN :#{#cond.techStacks}) AND
            (:#{#cond.tags} IS NULL OR t.id IN :#{#cond.tags})
        GROUP BY r.id
        HAVING (:#{#cond.techStacks} IS NULL OR COUNT(DISTINCT ts.id) = :#{#cond.techStackCount}) AND
            (:#{#cond.tags} IS NULL OR COUNT(DISTINCT t.id) = :#{#cond.tagCount})
        ORDER BY r.modifiedAt DESC
        """)
    List<Long> findIdsBySearchConditions(@Param("cond") RecruitmentSearchConditionDTO cond);

    /**
     * ID 목록에 해당하는 채용 공고를 조회합니다.
     *
     * @param ids ID 목록
     * @return 채용 공고 목록
     */
    @Query("""
        SELECT new org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO(
            r.id, r.recruitmentSourceId, r.title, c.companyName, c.companyImageUrl, r.dueDate,
            r.experienceMin, r.experienceMax, r.workLocation, r.latitude, r.longitude, r.modifiedAt
        )
        FROM Recruitment r
        JOIN r.company c
        WHERE r.id IN :ids
        ORDER BY r.modifiedAt DESC
        """)
    List<RecruitmentResponseDTO> findByIds(@Param("ids") List<Long> ids);
}
