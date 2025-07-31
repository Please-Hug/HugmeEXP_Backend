package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RecruitmentDetailResponseDTO {

    private Long id;
    private String title;
    private String companyName;
    private String companyImageUrl;
    private String companyAddress;
    private LocalDate establishmentDate;
    private String companyDescription;
    private LocalDateTime dueDate;
    private Integer experienceMin;
    private Integer experienceMax;
    private Integer education;
    private Integer salaryMin;
    private Integer salaryMax;
    private List<TechStackDTO> techStacks;
    private String advantage;
    private String qualifications;
    private String welfare;
    private String link;
    private List<TagDTO> tags;

    public static RecruitmentDetailResponseDTO from(Recruitment recruitment) {
        return RecruitmentDetailResponseDTO.builder()
                .id(recruitment.getId())
                .title(recruitment.getTitle())
                .companyName(recruitment.getCompany().getCompanyName())
                .companyImageUrl(recruitment.getCompany().getCompanyImageUrl())
                .companyAddress(recruitment.getCompany().getCompanyAddress())
                .establishmentDate(recruitment.getCompany().getEstablishmentDate())
                .companyDescription(recruitment.getCompany().getCompanyDescription())
                .dueDate(recruitment.getDueDate())
                .experienceMin(recruitment.getExperienceMin())
                .experienceMax(recruitment.getExperienceMax())
                .education(recruitment.getEducation())
                .salaryMin(recruitment.getSalaryMin())
                .salaryMax(recruitment.getSalaryMax())
                .techStacks(recruitment.getTechStacks().stream()
                        .map(ts -> TechStackDTO.builder()
                                .labelKo(ts.getTechItem().getKoreanName())
                                .labelEn(ts.getTechItem().getEnglishName())
                                .iconUrl(ts.getTechItem().getIconUrl())
                                .build())
                        .toList())
                .advantage(recruitment.getAdvantage())
                .qualifications(recruitment.getQualification())
                .welfare(recruitment.getWelfare())
                .link(recruitment.getLink())
                .tags(recruitment.getTags().stream()
                    .map(tag -> TagDTO.builder()
                                    .tagName(tag.getTagItem().getTagName())
                                    .build())
                            .toList())
                .build();
    }
}
