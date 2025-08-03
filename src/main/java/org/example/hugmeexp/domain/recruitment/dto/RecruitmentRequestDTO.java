package org.example.hugmeexp.domain.recruitment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @NotBlank
    private String sourceId;

    @NotBlank
    @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다")
    private String title;

    @Min(value = 0, message = "교육 수준은 0 이상이어야 합니다")
    @Max(value = 10, message = "교육 수준은 50 이하여야 합니다")
    private Integer education;

    @Min(value = 0, message = "최소 경력은 0 이상이어야 합니다")
    private Integer experienceMin;

    @Min(value = 0, message = "최대 경력은 0 이상이어야 합니다")
    private Integer experienceMax;

    @Size(max = 10000, message = "자격요건은 10000자를 초과할 수 없습니다")
    private String qualification;

    @Size(max = 10000, message = "우대사항은 10000자를 초과할 수 없습니다")
    private String advantage;

    @Size(max = 10000, message = "복리후생은 10000자를 초과할 수 없습니다")
    private String welfare;

    @NotBlank
    @Size(max = 500, message = "근무지는 500자를 초과할 수 없습니다")
    private String workLocation;

    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다")
    @Digits(integer = 2, fraction = 8, message = "위도는 소수점 8자리까지 가능합니다")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다")
    @Digits(integer = 3, fraction = 8, message = "경도는 소수점 8자리까지 가능합니다")
    private BigDecimal longitude;

    @NotNull
    @Min(value = 0, message = "최소 급여는 0 이상이어야 합니다")
    private Integer salaryMin;

    @NotNull
    @Min(value = 0, message = "최대 급여는 0 이상이어야 합니다")
    private Integer salaryMax;

    private String link;

    @NotNull
    private SourceType source;

    private LocalDateTime dueDate;

    @NotNull
    @Valid
    private CompanyRequestDTO company;

    @Valid
    private List<TechItemRequestDTO> requiredSkills;

    @Valid
    private List<TagRequestDTO> tags;

    public Recruitment toEntity() {
        return Recruitment.builder()
                .sourceId(sourceId)
                .title(title)
                .education(education)
                .experienceMin(experienceMin)
                .experienceMax(experienceMax)
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
                .company(company != null ? company.toEntity() : null)
                .build();
    }
}
