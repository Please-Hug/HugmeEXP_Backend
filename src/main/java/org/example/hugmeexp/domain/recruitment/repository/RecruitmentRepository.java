package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("""
        SELECT new org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO(
            r.id, r.title, c.companyName, c.companyImageUrl, r.dueDate, r.experience,
            r.workLocation, r.latitude, r.longitude
        )
        FROM Recruitment r
        JOIN r.company c
        LEFT JOIN r.techStacks ts
        LEFT JOIN r.tags t
        WHERE (:#{#cond.salaryMin} IS NULL OR r.salaryMin >= :#{#cond.salaryMin}) AND
              (:#{#cond.salaryMax} IS NULL OR r.salaryMax <= :#{#cond.salaryMax}) AND
              (:#{#cond.experience} IS NULL OR r.experience = :#{#cond.experience}) AND
              (:#{#cond.education} IS NULL OR r.education = :#{#cond.education}) AND
              (:#{#cond.workLocation} IS NULL OR r.workLocation LIKE %:#{#cond.workLocation}%) AND
              (:#{#cond.topLeftLat} IS NULL OR r.latitude >= :#{#cond.topLeftLat}) AND
              (:#{#cond.topLeftLng} IS NULL OR r.longitude >= :#{#cond.topLeftLng}) AND
              (:#{#cond.bottomRightLat} IS NULL OR r.latitude <= :#{#cond.bottomRightLat}) AND
              (:#{#cond.bottomRightLng} IS NULL OR r.longitude <= :#{#cond.bottomRightLng}) AND
              (:#{#cond.techStacks} IS NULL OR ts.id IN :#{#cond.techStacks}) AND
              (:#{#cond.tags} IS NULL OR t.id IN :#{#cond.tags})
        GROUP BY r.id, r.title, c.companyName, c.companyImageUrl, r.dueDate, r.experience,
                 r.workLocation, r.latitude, r.longitude
        HAVING (:#{#cond.techStacks} IS NULL OR COUNT(DISTINCT ts.id) = :#{#cond.techStackCount}) AND
               (:#{#cond.tags} IS NULL OR COUNT(DISTINCT t.id) = :#{#cond.tagCount})
    """)
    List<RecruitmentResponseDTO> findBySearchConditions(@Param("cond") RecruitmentSearchConditionDTO cond);

}
