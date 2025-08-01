package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.enums.SourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentRequestDTO {
    private String sourceId;
    private String title;
    private Integer education;
    private Integer experience;
    private String qualification;
    private String advantage;
    private String welfare;
    private String workLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer salaryMin;
    private Integer salaryMax;
    private String link;
    private SourceType source;
    private LocalDateTime dueDate;
    private CompanyRequestDTO company;
    private List<TechItemRequestDTO> requiredSkills;
    private List<TagRequestDTO> tags;

    public Recruitment toEntity() {
        return Recruitment.builder()
                .sourceId(sourceId)
                .title(title)
                .education(education)
                .experience(experience)
                .qualification(qualification)
                .advantage(advantage)
                .welfare(welfare)
                .workLocation(workLocation)
                .latitude(latitude)
                .longitude(longitude)
                .salaryMin(salaryMin)
                .salaryMax(salaryMax)
                .link(link)
                .source(source)
                .dueDate(dueDate)
                .company(company.toEntity())
                .build();
    }
}
