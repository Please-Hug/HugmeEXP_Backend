package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.Company;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RecruitmentCompanySearchResponseDTO {

    private Long companyId;
    private String companyName;

    public static RecruitmentCompanySearchResponseDTO from(Company company) {
        return RecruitmentCompanySearchResponseDTO.builder()
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .build();
    }

}
