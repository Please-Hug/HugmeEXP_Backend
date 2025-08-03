package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RecruitmentCompanySearchResponseDTO {

    private Long recruitmentId;
    private Long recruitmentSourceId;
    private String title;
    private Long companyId;
    private Long companySourceId;
    private String companyName;

    public static RecruitmentCompanySearchResponseDTO from(Recruitment recruitment) {
        Company company = recruitment.getCompany();

        return RecruitmentCompanySearchResponseDTO.builder()
                .recruitmentId(recruitment.getId())
                .recruitmentSourceId(recruitment.getRecruitmentSourceId())
                .title(recruitment.getTitle())
                .companyId(company.getId())
                .companySourceId(company.getCompanySourceId())
                .companyName(company.getCompanyName())
                .build();
    }

}
