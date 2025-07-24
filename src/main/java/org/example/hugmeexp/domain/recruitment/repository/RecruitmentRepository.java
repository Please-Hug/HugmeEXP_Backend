package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("SELECT new org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO( " +
            "r.id, r.title, c.companyName, c.companyImageUrl, r.dueDate, r.experience, " +
            "r.workLocation, r.latitude, r.longitude " +
            ") " +
            "FROM Recruitment r " +
            "JOIN r.company c " +
            "LEFT JOIN r.techStacks ts " +
            "LEFT JOIN r.tags t " +
            "WHERE (:salaryMin IS NULL OR r.salaryMin >= :salaryMin) AND " +
                    "(:salaryMax IS NULL OR r.salaryMax <= :salaryMax) AND " +
                    "(:experience IS NULL OR r.experience = :experience) AND " +
                    "(:education IS NULL OR r.education = :education) AND " +
                    "(:workLocation IS NULL OR r.workLocation LIKE %:workLocation%) AND " +
                    "(:topLeftLat IS NULL OR r.latitude >= :topLeftLat) AND " +
                    "(:topLeftLng IS NULL OR r.longitude <= :topLeftLng) AND " +
                    "(:bottomRightLat IS NULL OR r.latitude <= :bottomRightLat) AND " +
                    "(:bottomRightLng IS NULL OR r.longitude >= :bottomRightLng) AND " +
                    "(:techStacks IS NULL OR ts.id IN :techStacks) AND " +
                    "(:tags IS NULL OR t.id IN :tags) " +
            "GROUP BY r.id, r.title, c.companyName, c.companyImageUrl, r.dueDate, r.experience, " +
                        "r.workLocation, r.latitude, r.longitude " +
            "HAVING (:techStacks IS NULL OR COUNT(DISTINCT ts.id) = :techStackCount) AND " +
                    "(:tags IS NULL OR COUNT(DISTINCT t.id) = :tagCount)")
    List<RecruitmentResponseDTO> findBySearchConditions(
            Integer salaryMin,
            Integer salaryMax,
            Integer experience,
            Integer education,
            String workLocation,
            BigDecimal topLeftLat,
            BigDecimal topLeftLng,
            BigDecimal bottomRightLat,
            BigDecimal bottomRightLng,
            List<Long> techStacks,
            List<Long> tags,
            @Param("techStackCount") Long techStackCount,
            @Param("tagCount") Long tagCount
    );
}
